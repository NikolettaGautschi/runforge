// src/pages/MembersPage.tsx
import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Pencil, Trash2, UserCheck, UserX } from 'lucide-react';
import { memberApi } from '../services/api';
import type { Member, RunningLevel } from '../types';
import { LEVEL_LABELS } from '../types';

const emptyMember: Omit<Member, 'id' | 'joinedDate'> = {
  firstName: '', lastName: '', email: '', phone: '',
  level: 'BEGINNER', active: true,
};

export default function MembersPage() {
  const qc = useQueryClient();
  const [modal, setModal] = useState<{ open: boolean; editing: Member | null }>({ open: false, editing: null });
  const [form, setForm] = useState(emptyMember);

  const { data: members = [], isLoading } = useQuery({ queryKey: ['members'], queryFn: memberApi.getAll });

  const createMut = useMutation({
    mutationFn: memberApi.create,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['members'] }); qc.invalidateQueries({ queryKey: ['stats'] }); closeModal(); },
  });
  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<Member> }) => memberApi.update(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['members'] }); qc.invalidateQueries({ queryKey: ['stats'] }); closeModal(); },
  });
  const deleteMut = useMutation({
    mutationFn: memberApi.delete,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['members'] }); qc.invalidateQueries({ queryKey: ['stats'] }); },
  });

  const openCreate = () => { setForm(emptyMember); setModal({ open: true, editing: null }); };
  const openEdit = (m: Member) => {
    setForm({ firstName: m.firstName, lastName: m.lastName, email: m.email, phone: m.phone, level: m.level, active: m.active });
    setModal({ open: true, editing: m });
  };
  const closeModal = () => setModal({ open: false, editing: null });

  const submit = () => {
    if (modal.editing) updateMut.mutate({ id: modal.editing.id, data: form });
    else createMut.mutate(form);
  };

  const levelClass = (l: RunningLevel) => l.toLowerCase();

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Mitglieder</div>
          <div className="page-subtitle">{members.length} Mitglieder gesamt</div>
        </div>
        <button className="btn btn-primary" onClick={openCreate}><Plus size={16} /> Mitglied hinzufügen</button>
      </div>

      <div className="card">
        {isLoading ? <div className="loading">Lade Mitglieder...</div> : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Name</th><th>E-Mail</th><th>Telefon</th><th>Level</th><th>Status</th><th>Beigetreten</th><th></th>
                </tr>
              </thead>
              <tbody>
                {members.length === 0 ? (
                  <tr><td colSpan={7} className="empty">Keine Mitglieder vorhanden</td></tr>
                ) : members.map(m => (
                  <tr key={m.id}>
                    <td><strong>{m.firstName} {m.lastName}</strong></td>
                    <td style={{ color: '#8b98b1' }}>{m.email}</td>
                    <td style={{ color: '#8b98b1' }}>{m.phone || '—'}</td>
                    <td><span className={`badge ${levelClass(m.level)}`}>{LEVEL_LABELS[m.level]}</span></td>
                    <td>
                      {m.active
                        ? <span className="badge active"><UserCheck size={11} style={{ marginRight: 4 }} />Aktiv</span>
                        : <span className="badge inactive"><UserX size={11} style={{ marginRight: 4 }} />Inaktiv</span>}
                    </td>
                    <td style={{ color: '#8b98b1' }}>{m.joinedDate}</td>
                    <td>
                      <div style={{ display: 'flex', gap: 4 }}>
                        <button className="btn btn-ghost" onClick={() => openEdit(m)} style={{ padding: '6px 10px' }}><Pencil size={14} /></button>
                        <button className="btn btn-danger" onClick={() => deleteMut.mutate(m.id)} style={{ padding: '6px 10px' }}><Trash2 size={14} /></button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {modal.open && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-title">{modal.editing ? 'Mitglied bearbeiten' : 'Neues Mitglied'}</div>
            <div className="form-grid">
              <div className="form-group">
                <label>Vorname</label>
                <input value={form.firstName} onChange={e => setForm(f => ({ ...f, firstName: e.target.value }))} placeholder="Anna" />
              </div>
              <div className="form-group">
                <label>Nachname</label>
                <input value={form.lastName} onChange={e => setForm(f => ({ ...f, lastName: e.target.value }))} placeholder="Müller" />
              </div>
              <div className="form-group full">
                <label>E-Mail</label>
                <input type="email" value={form.email} onChange={e => setForm(f => ({ ...f, email: e.target.value }))} placeholder="anna@beispiel.ch" />
              </div>
              <div className="form-group">
                <label>Telefon</label>
                <input value={form.phone} onChange={e => setForm(f => ({ ...f, phone: e.target.value }))} placeholder="+41 79 123 45 67" />
              </div>
              <div className="form-group">
                <label>Level</label>
                <select value={form.level} onChange={e => setForm(f => ({ ...f, level: e.target.value as RunningLevel }))}>
                  <option value="BEGINNER">Einsteiger</option>
                  <option value="INTERMEDIATE">Fortgeschritten</option>
                  <option value="ADVANCED">Experte</option>
                </select>
              </div>
            </div>
            <div className="modal-actions">
              <button className="btn btn-ghost" onClick={closeModal}>Abbrechen</button>
              <button className="btn btn-primary" onClick={submit}>{modal.editing ? 'Speichern' : 'Erstellen'}</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
