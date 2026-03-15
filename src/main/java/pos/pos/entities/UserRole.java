package pos.pos.entities;

public enum UserRole {
    ROLE_ADMIN,    // Hace de todo y corrige stock
    ROLE_CASHIER,  // Solo factura y ve su caja
    ROLE_WAREHOUSE, // Solo pone "cantidad en existencia"
    ROLE_SUPPLIER  // Solo ve sus premios y facturas
}
