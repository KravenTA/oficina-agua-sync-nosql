-- HU-55: recibos y pagos de ejemplo para probar la replicación de pagos.
-- Los seeders del monolito no crean recibos ni pagos; estos datos solo existen en el entorno local.

SET NAMES utf8mb4;

INSERT INTO recibos (id, lectura_id, numero, fecha_emision, fecha_vencimiento, monto_consumo, monto_adicional, concepto_adicional, total, estado, created_at, updated_at) VALUES
  (1, 1,  'REC-000001', '2026-08-01 08:00:00', '2026-08-31', 15.00, 0.00, NULL, 15.00, 'pagado',    '2026-08-01 08:00:00', '2026-08-05 10:15:00'),
  (2, 3,  'REC-000002', '2026-08-01 08:00:00', '2026-08-31', 25.00, 0.00, NULL, 25.00, 'pagado',    '2026-08-01 08:00:00', '2026-08-07 09:30:00'),
  (3, 4,  'REC-000003', '2026-08-01 08:00:00', '2026-08-31', 45.00, 0.00, NULL, 45.00, 'pagado',    '2026-08-01 08:00:00', '2026-08-10 11:00:00'),
  (4, 6,  'REC-000004', '2026-08-01 08:00:00', '2026-08-31', 18.00, 0.00, NULL, 18.00, 'pendiente', '2026-08-01 08:00:00', '2026-08-01 08:00:00'),
  (5, 7,  'REC-000005', '2026-08-01 08:00:00', '2026-08-31', 25.00, 5.00, 'Mora', 30.00, 'pagado', '2026-08-01 08:00:00', '2026-09-03 14:20:00'),
  (6, 9,  'REC-000006', '2026-08-01 08:00:00', '2026-08-31', 45.00, 0.00, NULL, 45.00, 'vencido',   '2026-08-01 08:00:00', '2026-09-01 00:00:00');

INSERT INTO pagos (id, lectura_id, recibo_id, usuario_id, monto, fecha_pago, metodo, referencia, created_at, updated_at) VALUES
  (1, 1, 1, 2, 15.00, '2026-08-05 10:15:00', 'efectivo',      NULL,           '2026-08-05 10:15:00', '2026-08-05 10:15:00'),
  (2, 3, 2, 2, 25.00, '2026-08-07 09:30:00', 'transferencia', 'TRX-88231',    '2026-08-07 09:30:00', '2026-08-07 09:30:00'),
  (3, 4, 3, 2, 45.00, '2026-08-10 11:00:00', 'efectivo',      NULL,           '2026-08-10 11:00:00', '2026-08-10 11:00:00'),
  (4, 7, 5, 2, 30.00, '2026-09-03 14:20:00', 'transferencia', 'TRX-90412',    '2026-09-03 14:20:00', '2026-09-03 14:20:00');
