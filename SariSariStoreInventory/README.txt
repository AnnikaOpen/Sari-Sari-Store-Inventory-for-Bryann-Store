SARI-SARI STORE INVENTORY (Java Swing + MySQL)

SETUP
1. XAMPP Control Panel: START MySQL (and Apache for phpMyAdmin).
2. phpMyAdmin > SQL tab > paste database/sari_sari_store.sql > Go.
3. NetBeans: right-click Libraries > Add Library > "MySQL JDBC Driver".
4. In NetBeans DELETE the old files in package sarisaristore (especially InventoryForm.java),
   then paste ALL 21 files from src/sarisaristore.
5. Run (F6).  Login: admin / admin123

SCREENS (tabs)
Products | New Sale | Sales History | Customers | Staff

TABLES USED: PRODUCT, STAFF, CUSTOMER, SALE, SALE_ITEM (+ LOGIN_ACCOUNT for the login screen)
Database settings are at the top of DBConnection.java.
