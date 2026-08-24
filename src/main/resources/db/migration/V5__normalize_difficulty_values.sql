UPDATE route SET difficulty = 'FACIL'    WHERE UPPER(difficulty) IN ('BAJA', 'FACIL');
UPDATE route SET difficulty = 'MODERADA' WHERE UPPER(difficulty) IN ('MEDIA', 'MODERADA');
UPDATE route SET difficulty = 'DIFICIL'  WHERE UPPER(difficulty) IN ('ALTA', 'DIFICIL');