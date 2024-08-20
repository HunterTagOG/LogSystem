[![](https://jitpack.io/v/HunterTagOG/LogSystem.svg)](https://jitpack.io/#HunterTagOG/LogSystem)

# LogSystem

LogSystem ist ein Minecraft-Plugin, das eine API zur Verwaltung und Protokollierung von Spielerereignissen bereitstellt. Es ermöglicht das einfache Speichern, Aktualisieren und Abrufen von Ereignissen wie Blockplatzierungen, Spielerinteraktionen und mehr.

## Funktionen

- Unterstützung für Bukkit/Spigot/Paper (1.20.x)
- Einfach zu verwendende API für das Protokollieren von Spielerereignissen
- Unterstützung für MySQL-Datenbanken mit HikariCP-Verbindungspool
- Automatisches Speichern und Laden von Spielerlogs
- Konfigurierbare Ereignistypen und Speicherintervalle

## Importing

Wir verwenden JitPack, um die neueste Version von LogSystem automatisch zu kompilieren und zu hosten. Um LogSystem mit Maven zu installieren, öffnen Sie Ihre `pom.xml`, suchen Sie den `<repositories>` Abschnitt und fügen Sie dieses Repository hinzu:

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

Suchen Sie dann den <dependencies> Abschnitt Ihrer pom.xml und fügen Sie Folgendes hinzu. Ersetzen Sie "REPLACE_WITH_LATEST_VERSION" durch die neueste Version von: Github

```xml
<dependency>
    <groupId>com.github.HunterTagOG</groupId>
    <artifactId>LogSystem</artifactId>
    <version>REPLACE_WITH_LATEST_VERSION</version>
    <scope>provided</scope>
</dependency>
```

Für weitere Informationen, einschließlich der Verwendung von LogSystem mit anderen Tools als Maven, besuchen Sie bitte: Jitpack

### Shading (wichtig!)

Siehe Schritt 2 im Quick Start Guide unten.

LogSystem kommt mit einigen Bibliotheken, die für Sie verfügbar sind, z.B. HikariCP, um sicherzustellen, dass sie beim Codieren zugänglich sind, aber nicht als Abhängigkeiten hinzugefügt werden müssen.

Maven hat eine Einschränkung, bei der diese Bibliotheken in Ihrer Plugin-JAR-Datei landen, wenn Sie die `<includes>` Sektion des maven-shade-plugin nicht richtig konfigurieren.

Kopieren Sie den folgenden Abschnitt und fügen Sie ihn in Ihren `<plugins>` Abschnitt der pom.xml ein (wenn Sie bereits einen solchen Abschnitt haben, entfernen Sie ihn).

Wenn Sie eine Abhängigkeit in Ihr Jar kompilieren möchten, installieren Sie sie normal über die `<dependency>` Direktive, setzen Sie ihren Scope auf "compile" und fügen Sie sie dann erneut hinzu. Sie können einfach die `<include>` duplizieren und für Ihre Abhängigkeit ändern.

### Wichtig

Nach dem Erzeugen der JAR achten Sie darauf, die JAR mit folgendem Kürzel `-shaded.jar` am Ende zu verwenden, wie in folgendem Beispiel: `plugin-name-1.0-shaded.jar`

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.4</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <createDependencyReducedPom>false</createDependencyReducedPom>
        <artifactSet>
            <includes>
                <include>de.huntertagog.locobroko.LogSystem</include>
            </includes>
        </artifactSet>
    </configuration>
</plugin>
```

## Quick Start

1. Importieren Sie LogSystem mit Maven oder Gradle (siehe Importing).
2. **WICHTIG**: Konfigurieren Sie das Shading, um nur LogSystem und die benötigten Bibliotheken einzuschließen, damit nicht alle Abhängigkeiten in Ihr Jar aufgenommen werden.
3. Registrieren Sie die LogManagerAPI in Ihrem Plugin:

    ```java
    import de.huntertagog.locobroko.api.LogManagerAPI;
    import de.huntertagog.locobroko.registry.PlayerData;
    import org.bukkit.plugin.java.JavaPlugin;

    import java.util.Map;
    import java.util.UUID;

    public class MyPlugin extends JavaPlugin {

    private static LogManagerAPI logManagerAPI;

    @Override
    public void onEnable() {
        logManagerAPI = (LogManagerAPI) getServer().getPluginManager().getPlugin("LogSystem");
        if (logManagerAPI != null) {
            // Verwenden Sie die API
            UUID playerUUID = ...; // Die UUID des Spielers

            // Abrufen der PlayerData
            PlayerData playerData = logManagerAPI.getPlayerData(playerUUID);
            if (playerData != null) {
                getLogger().info("Player Name: " + playerData.getPlayerName());
                Map<String, Map<String, Integer>> logs = playerData.getLogs();
                // Verwende die Logs wie gewünscht
            }

            // Anzahl eines bestimmten Ereignistyps abrufen
            int eventCount = logManagerAPI.getEventCount(playerUUID, "block_break", "STONE");
            getLogger().info("Block Break Count (STONE): " + eventCount);

            // Alle Ereignisse eines bestimmten Typs abrufen
            Map<String, Integer> eventLogs = logManagerAPI.getEventLogs(playerUUID, "block_break");
            eventLogs.forEach((eventData, count) -> {
                getLogger().info(eventData + ": " + count);
            });

            // Spielername abrufen
            String playerName = logManagerAPI.getPlayerName(playerUUID);
            getLogger().info("Player Name: " + playerName);
        } else {
            getLogger().severe("LogManagerAPI not found!");
        }
    }
}

    ```

## Methoden

`PlayerData getPlayerData(UUID playerUUID)`
Gibt die `PlayerData`-Instanz für einen bestimmten Spieler zurück, die den Spielernamen und alle Logs enthält.

`int getEventCount(UUID playerUUID, String eventType, String eventData)`
Gibt die Anzahl eines bestimmten Ereignisses eines Spielers zurück.

`Map<String, Integer> getEventLogs(UUID playerUUID, String eventType)`
Gibt alle Ereignisse eines bestimmten Typs eines Spielers zurück.

`String getPlayerName(UUID playerUUID)`
Gibt den Namen eines bestimmten Spielers basierend auf seiner UUID zurück.

## Konfiguration

Die Konfiguration des LogSystem-Plugins erfolgt über die `config.yml`-Datei. Hier ist ein Beispiel:

```yaml
# LogSystem Configuration

# Database connection settings
database:
  # The JDBC URL of your MySQL database
  # Example: jdbc:mysql://localhost:3306/minecraft
  url: jdbc:mysql://localhost:3306/minecraft

  # The username for accessing your MySQL database
  user: yourDatabaseUser

  # The password for accessing your MySQL database
  password: yourDatabasePassword

# Event logging settings
logging_enabled: true

# Save interval in minutes
save_interval: 10
```

## Kompatibilität

Wir bemühen uns, eine breite Kompatibilität zu gewährleisten, die die folgenden Minecraft-Versionen unterstützt und weitere werden folgen:

- 1.20.x

LogSystem funktioniert auf Bukkit, Spigot und Paper.

## Lizenzinformationen

© 2024 HUNTER DEVELOPMENT

Mit der MIT-Lizenz können Sie alles tun, was Sie möchten, solange Sie die ursprünglichen Urheberrechtsvermerke und Haftungsausschlüsse beibehalten. Eine Kopie der Lizenz finden Sie im Repository.
