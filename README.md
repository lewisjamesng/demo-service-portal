# demo-service-portal

Service appointment API: book, look up and clash-check service-centre appointments over a small JSON HTTP API. **Synthetic demo repository.** All code and data are fictional sample material for a security-remediation demo.

**Owner:** `@demo-org/service-experience-team` (see `.github/CODEOWNERS`)

## API

The JSON contract uses snake_case field names (`appointment_id`, `vehicle_id`, `service_centre`, `slot_start`, `service_type`, `customer_note`).

| Method | Path | Description |
|---|---|---|
| GET | `/health` | Liveness check |
| POST | `/appointments` | Book an appointment |
| GET | `/appointments/{id}` | Fetch an appointment |

## Develop

Requires Java 17 and Maven 3.9+.

```bash
mvn -B verify
```

## Security scanning

CI runs `osv-scanner` in the `security-scan` job. Reports are uploaded as artifacts, and the SARIF goes to GitHub code scanning.

## Licence

MIT
