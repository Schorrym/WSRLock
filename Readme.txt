1. Vor Programmstart ist das SQL-File "WSRLock1.sql" auszuführen um die Datenbank und den zugehörigen Nutzer zu erstellen.
2. Das Projekt kannn nun auf dem Tomcat gestartet werden. Die JPA Schnittstelle erstellt automatisch alle nötigen Tabellen.
3. Nach erfolgreichem Start ist das zweite SQL-File "WSRLock2.sql" auszuführen um zwei Testbenutzer und Rollen hinzuzufügen.
-- Die Nutzer sind: "Alice" und "Bob" und besitzen jeweils das Passwort "test1"