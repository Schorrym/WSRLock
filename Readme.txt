1. Vor Programmstart ist das SQL-File "WSRLock1.sql" auszuf�hren um die Datenbank und den zugeh�rigen Nutzer zu erstellen.
2. Das Projekt kannn nun auf dem Tomcat gestartet werden. Die JPA Schnittstelle erstellt automatisch alle n�tigen Tabellen.
	- Entweder die "WSRLock.war" Datei in den "/webapps" Ordner von Tomcat kopieren.
	- Oder mit Maven den Programmordner mit den Befehlen "compile" und "install" ausf�hrbar machen und anschlie�end manuell auf dem Tomcat starten.
3. Nach erfolgreichem Start ist das zweite SQL-File "WSRLock2.sql" auszuf�hren um zwei Testbenutzer und Rollen hinzuzuf�gen.
-- Die Nutzer sind: "Alice" und "Bob" und besitzen jeweils das Passwort "test1"