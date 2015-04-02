You'll need to increase PermGen space for it to work on large libraries, like Eclipse, Android, or Spring. Suggested arguments are:

-Xms4G -Xmx4G -ea -XX:PermSize=128m -XX:MaxPermSize=128m