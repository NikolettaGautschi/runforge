/**
 * src/pages/PlansPage.tsx — Training plans management page
 *
 * Provides a full CRUD interface for managing training plans:
 * - Grid view of plans displayed as cards with level badges
 * - Card displays: plan name, description, level, duration, weekly km
 * - Modal form for creating and editing plans
 * - Delete functionality with hard delete
 * - Real-time query cache synchronization with dashboard
 *
 * Uses TanStack Query for server state and React hooks for form state.
 */

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Pencil, Trash2, Timer, Route } from 'lucide-react';
import { planApi } from '../services/api';
import type { RunningPlan, RunningLevel } from '../types';
import { LEVEL_LABELS, LEVEL_COLORS } from '../types';

/**
 * Empty plan template for form initialization (create mode).
 */
const emptyPlan: Omit<RunningPlan, 'id' | 'createdDate'> = {
  name: '', description: '', level: 'BEGINNER', durationWeeks: 8, weeklyKm: 20,
};

/**
 * PlansPage component — Renders training plan CRUD interface.
 *
 * @returns JSX element with grid layout and modal form
 */
export default function PlansPage() {
  // Query client for manual cache invalidation
  const qc = useQueryClient();
  const [modal, setModal] = useState<{ open: boolean; editing: RunningPlan | null }>({ open: false, editing: null });
  const [form, setForm] = useState(emptyPlan);

  const { data: plans = [], isLoading } = useQuery({ queryKey: ['plans'], queryFn: planApi.getAll });

  const createMut = useMutation({ mutationFn: planApi.create, onSuccess: () => { qc.invalidateQueries({ queryKey: ['plans'] }); qc.invalidateQueries({ queryKey: ['stats'] }); closeModal(); } });
  const updateMut = useMutation({ mutationFn: ({ id, data }: { id: number; data: Partial<RunningPlan> }) => planApi.update(id, data), onSuccess: () => { qc.invalidateQueries({ queryKey: ['plans'] }); closeModal(); } });
  const deleteMut = useMutation({ mutationFn: planApi.delete, onSuccess: () => { qc.invalidateQueries({ queryKey: ['plans'] }); qc.invalidateQueries({ queryKey: ['stats'] }); } });

  const openCreate = () => { setForm(emptyPlan); setModal({ open: true, editing: null }); };
  const openEdit = (p: RunningPlan) => {
    setForm({ name: p.name, description: p.description, level: p.level, durationWeeks: p.durationWeeks, weeklyKm: p.weeklyKm });
    setModal({ open: true, editing: p });
  };

  /**
   * Close modal and reset form.
   */
  const closeModal = () => setModal({ open: false, editing: null });
  const submit = () => { if (modal.editing) updateMut.mutate({ id: modal.editing.id, data: form }); else createMut.mutate(form); };

  return (
    <div>
      {/* Page Header */}
      <div className="page-header">
        <div>
          <div className="page-title">Trainingspläne</div>
          <div className="page-subtitle">{plans.length} Pläne verfügbar</div>
        </div>
        <button className="btn btn-primary" onClick={openCreate}><Plus size={16} /> Plan erstellen</button>
      </div>

      {isLoading ? <div className="loading">Lade Pläne...</div> : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 16 }}>
          {plans.length === 0 ? <div className="empty">Keine Pläne vorhanden</div> : plans.map(p => (
            <div key={p.id} className="card" style={{ margin: 0 }}>
              {/* Card Header with Level Badge and Actions */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
                <span className={`badge ${p.level.toLowerCase()}`} style={{ color: LEVEL_COLORS[p.level] }}>
                  {LEVEL_LABELS[p.level]}
                </span>

                {/* Edit and Delete Buttons */}
                <div style={{ display: 'flex', gap: 4 }}>
                  <button className="btn btn-ghost" onClick={() => openEdit(p)} style={{ padding: '4px 8px' }}><Pencil size={13} /></button>
                  <button className="btn btn-danger" onClick={() => deleteMut.mutate(p.id)} style={{ padding: '4px 8px' }}><Trash2 size={13} /></button>
                </div>
              </div>tits

              {/* Plan Name */}
              <div style={{ fontSize: 18, fontWeight: 700, marginBottom: 6 }}>
                {p.name}
              </div>
              <div style={{ fontSize: 18, fontWeight: 700, marginBottom: 6 }}>{p.name}</div>
              <div style={{ fontSize: 13, color: '#8b98b1', marginBottom: 16, lineHeight: 1.5 }}>{p.description || '—'}</div>
            </div>
          ))}
        </div>
      )}

      {/* Create/Edit Modal */}
      {modal.open && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-title">{modal.editing ? 'Plan bearbeiten' : 'Neuer Trainingsplan'}</div>
            <div className="form-grid">
              {/* Plan Name */}
              <div className="form-group full">
                <label>Name</label>
                <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} placeholder="5km Einsteiger-Plan" />
              </div>

              {/* Plan Description */}
              <div className="form-group full">
                <label>Beschreibung</label>
                <textarea value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} rows={3} placeholder="Beschreibung des Plans..." style={{ resize: 'vertical' }} />
              </div>

              {/* Target Level */}
              <div className="form-group">
                <label>Level</label>
                <select value={form.level} onChange={e => setForm(f => ({ ...f, level: e.target.value as RunningLevel }))}>
                  <option value="BEGINNER">Einsteiger</option>
                  <option value="INTERMEDIATE">Fortgeschritten</option>
                  <option value="ADVANCED">Experte</option>
                </select>
              </div>
            </div>

            {/* Modal Actions */}
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
