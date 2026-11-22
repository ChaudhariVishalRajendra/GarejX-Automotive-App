# GarejX Admin Frontend

React + Vite admin console for the supplied GarejX backend.

## Run

1. Start MongoDB and the backend:
   `cd garejx-backend && npm install && npm run dev`
2. Start this frontend in another terminal:
   `npm install`
   `npm run dev`
3. Open `http://localhost:5173`.

Default API URL: `http://localhost:5000/api`.
You can override it with `VITE_API_URL` in `.env`.

## Admin features

- ADMIN-only login using `/api/auth/login`
- Dashboard
- Customers & Staff listing
- Staff creation via admin `/api/users/staff` (ADMIN or MECHANIC)
- Mechanics
- Vehicles
- Services CRUD
- Appointments and mechanic assignment
- Bills and payment updates
- Garage settings
- Change password for the currently signed-in admin via `/api/auth/change-password`
- Error boundary and safe async effects to prevent white-screen crashes

## Customer creation note

The supplied backend does **not** expose an admin-protected customer creation endpoint. It exposes public `/api/auth/register`, which creates `USER` accounts. The admin panel therefore does not present that public route as an admin-only "Add Customer" action. If you want a secure admin-only customer creation button, add a backend route such as `POST /api/users/customer` protected by `allowRoles("ADMIN")`, then connect the form to it.
