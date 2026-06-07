/**
 * src/pages/MembersPage.tsx — Members management page
 *
 * Provides a full CRUD interface for managing running group members:
 * - Table view of all members with level badges and status indicators
 * - Modal form for creating and editing members
 * - Soft-delete functionality (trash icon)
 * - Real-time query cache synchronization with dashboard
 *
 * Uses TanStack Query for server state and React hooks for form state.
 */

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Pencil, Trash2, UserCheck, UserX } from 'lucide-react';
import { memberApi } from '../services/api';
import type { Member, RunningLevel } from '../types';
import { LEVEL_LABELS } from '../types';

/**
 * Empty member template for form initialization (create mode).
 */
const emptyMember: Omit<Member, 'id' | 'joinedDate'> = {
  firstName: '', lastName: '', email: '', phone: '',
  level: 'BEGINNER', active: true,
};

/**
 * MembersPage component — Renders member CRUD interface.
 *
 * @returns JSX element with table and modal form
 */
export default function MembersPage() {
  // Query client for manual cache invalidation
  const qc = useQueryClient();

  // Modal state: open flag and editing context
  const [modal, setModal] = useState<{ open: boolean; editing: Member | null }>({ open: false, editing: null });

  // Form state for member data (create/edit)
  const [form, setForm] = useState(emptyMember);

  /**
   * Fetch all members.
   * - queryKey: ['members'] — invalidated on create/update/delete
   * - Triggers loading state while fetching
   */
  const { data: members = [], isLoading } = useQuery({
    queryKey: ['members'],
    queryFn: memberApi.getAll
  });

  /**
   * Create member mutation.
   * Invalidates 'members' and 'stats' queries on success to keep UI in sync.
   */
  const createMut = useMutation({
    mutationFn: memberApi.create,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['members'] });
      qc.invalidateQueries({ queryKey: ['stats'] });
      closeModal();
    },
  });

  /**
   * Update member mutation.
   * Wraps ID and data in object; invalidates caches on success.
   */
  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<Member> }) => memberApi.update(id, data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['members'] });
      qc.invalidateQueries({ queryKey: ['stats'] });
      closeModal();
    },
  });

  /**
   * Delete member mutation (soft-delete via API).
   * Invalidates caches on success.
   */
  const deleteMut = useMutation({
    mutationFn: memberApi.delete,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['members'] });
      qc.invalidateQueries({ queryKey: ['stats'] });
    },
  });

  /**
   * Open create modal with empty form.
   */
  const openCreate = () => {
    setForm(emptyMember);
    setModal({ open: true, editing: null });
  };

  /**
   * Open edit modal with member data pre-filled.
   * @param m — Member object to edit
   */
  const openEdit = (m: Member) => {
    setForm({
      firstName: m.firstName,
      lastName: m.lastName,
      email: m.email,
      phone: m.phone,
      level: m.level,
      active: m.active
    });
    setModal({ open: true, editing: m });
  };

  /**
   * Close modal and reset form.
   */
  const closeModal = () => setModal({ open: false, editing: null });

  /**
   * Submit form: either create or update depending on modal context.
   */
  const submit = () => {
    if (modal.editing) updateMut.mutate({ id: modal.editing.id, data: form });
    else createMut.mutate(form);
  };

  /**
   * Get CSS class name for level badge styling.
   * @param l — RunningLevel enum value
   * @returns CSS class name (e.g., "beginner")
   */
  const levelClass = (l: RunningLevel) => l.toLowerCase();

  return (
    <div>
      {/* Page Header */}
      <div className="page-header">
        <div>
          <div className="page-title">Mitglieder</div>
          <div className="page-subtitle">{members.length} Mitglieder gesamt</div>
        </div>
        <button className="btn btn-primary" onClick={openCreate}>
          <Plus size={16} /> Mitglied hinzufügen
        </button>
      </div>

      {/* Members Table */}
      <div className="card">
        {isLoading ? (
          <div className="loading">Lade Mitglieder...</div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>E-Mail</th>
                  <th>Telefon</th>
                  <th>Level</th>
                  <th>Status</th>
                  <th>Beigetreten</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {members.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="empty">Keine Mitglieder vorhanden</td>
                  </tr>
                ) : members.map(m => (
                  <tr key={m.id}>
                    {/* Member name (bold) */}
                    <td><strong>{m.firstName} {m.lastName}</strong></td>

                    {/* Email */}
                    <td style={{ color: '#8b98b1' }}>{m.email}</td>

                    {/* Phone (or dash if empty) */}
                    <td style={{ color: '#8b98b1' }}>{m.phone || '—'}</td>

                    {/* Level badge with color coding */}
                    <td>
                      <span className={`badge ${levelClass(m.level)}`}>
                        {LEVEL_LABELS[m.level]}
                      </span>
                    </td>

                    {/* Active status badge */}
                    <td>
                      {m.active
                        ? <span className="badge active">
                            <UserCheck size={11} style={{ marginRight: 4 }} />
                            Aktiv
                          </span>
                        : <span className="badge inactive">
                            <UserX size={11} style={{ marginRight: 4 }} />
                            Inaktiv
                          </span>
                      }
                    </td>

                    {/* Joined date */}
                    <td style={{ color: '#8b98b1' }}>{m.joinedDate}</td>

                    {/* Action buttons */}
                    <td>
                      <div style={{ display: 'flex', gap: 4 }}>
                        {/* Edit button */}
                        <button
                          className="btn btn-ghost"
                          onClick={() => openEdit(m)}
                          style={{ padding: '6px 10px' }}
                        >
                          <Pencil size={14} />
                        </button>

                        {/* Delete button */}
                        <button
                          className="btn btn-danger"
                          onClick={() => deleteMut.mutate(m.id)}
                          style={{ padding: '6px 10px' }}
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Create/Edit Modal */}
      {modal.open && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-title">
              {modal.editing ? 'Mitglied bearbeiten' : 'Neues Mitglied'}
            </div>

            {/* Form Fields Grid */}
            <div className="form-grid">
              <div className="form-group">
                <label>Vorname</label>
                <input
                  value={form.firstName}
                  onChange={e => setForm(f => ({ ...f, firstName: e.target.value }))}
                  placeholder="Anna"
                />
              </div>

              <div className="form-group">
                <label>Nachname</label>
                <input
                  value={form.lastName}
                  onChange={e => setForm(f => ({ ...f, lastName: e.target.value }))}
                  placeholder="Müller"
                />
              </div>

              <div className="form-group full">
                <label>E-Mail</label>
                <input
                  type="email"
                  value={form.email}
                  onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                  placeholder="anna@beispiel.ch"
                />
              </div>

              <div className="form-group">
                <label>Telefon</label>
                <input
                  value={form.phone}
                  onChange={e => setForm(f => ({ ...f, phone: e.target.value }))}
                  placeholder="+41 79 123 45 67"
                />
              </div>

              <div className="form-group">
                <label>Level</label>
                <select
                  value={form.level}
                  onChange={e => setForm(f => ({ ...f, level: e.target.value as RunningLevel }))}
                >
                  <option value="BEGINNER">Einsteiger</option>
                  <option value="INTERMEDIATE">Fortgeschritten</option>
                  <option value="ADVANCED">Experte</option>
                </select>
              </div>
            </div>

            {/* Modal Actions */}
            <div className="modal-actions">
              <button className="btn btn-ghost" onClick={closeModal}>Abbrechen</button>
              <button className="btn btn-primary" onClick={submit}>
                {modal.editing ? 'Speichern' : 'Erstellen'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
