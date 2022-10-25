package me.vaindev.jammybounties;

import me.vaindev.jammybounties.utils.TranslateBase64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataAccess {
    private static final Plugin plugin = JammyBounties.getPlugin(JammyBounties.class);
    private static final Logger log = plugin.getLogger();
    private static Connection conn;

    public static void connect() {
        try {
            String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/data.db";
            conn = DriverManager.getConnection(url);

            log.info("Connection to SQLite has been established!");
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
    }

    public static void disconnect() {
        try {
            conn.close();
        } catch (SQLException e) {}
    }

    public static void initDb() throws SQLException, IOException {
        connect();
        String setup;
        try (InputStream in = plugin.getResource("dbsetup.sql")) {
            setup = new String(in.readAllBytes());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not read db setup file.", e);
            throw e;
        }
        String[] queries = setup.split(";");

        for (String query : queries) {
            if (query.isBlank()) continue;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();
            }
        }
        log.info("§2Database setup complete.");
    }

    public static boolean setBounty(UUID uuid, ItemStack[] items, double eco) {
        String formattedItems = TranslateBase64.toBase64(items);

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO bounties(uuid, items, eco, datecreated) VALUES(?, ?, ?, DateTime('now'))")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, formattedItems);
            stmt.setDouble(3, eco);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            log.warning("Could not add bounty to database. Error: " + e);
        }
        return false;
    }

    public static boolean appendBounty(UUID uuid, ItemStack[] newItems, double newEco) {
        String formattedItems;
        double dbEco;
        ItemStack[] dbItems;

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT items, eco FROM bounties WHERE uuid = ?;"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next())
                return false;
            formattedItems = resultSet.getString("items");
            dbEco = resultSet.getDouble("eco");
        } catch (SQLException e) {
            log.warning("Could not retrieve bounty. Error: " + e);
            return false;
        }

        try {
            dbItems = TranslateBase64.fromBase64(formattedItems);
        } catch (IOException e) {
            log.warning("Binary value could not be converted.");
            return false;
        }

        removeBounty(uuid);

        List<ItemStack> updatedItems = new ArrayList<>();
        updatedItems.addAll(List.of(dbItems));
        updatedItems.addAll(List.of(newItems));
        String updatedFormattedItems = TranslateBase64.toBase64(updatedItems.toArray(ItemStack[]::new));
        double updatedEco = dbEco + newEco;

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO bounties(uuid, items, eco, datecreated) VALUES(?, ?, ?, DateTime('now'))")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, updatedFormattedItems);
            stmt.setDouble(3, updatedEco);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            log.warning("Could not add bounty to database. Error: " + e);
        }
        return false;
    }

    public static List<Bounty> getBounties() {
        List<Bounty> bounties = new ArrayList<>();
        String formattedItems;
        String uuidString;
        double eco;
        ItemStack[] items;
        Date dateTime;
        UUID uuid;

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM bounties;"
        )) {
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                uuidString = resultSet.getString("uuid");
                formattedItems = resultSet.getString("items");
                uuid = UUID.fromString(uuidString);
                items = TranslateBase64.fromBase64(formattedItems);
                eco = resultSet.getDouble("eco");
                dateTime = resultSet.getDate("datecreated");
                Bounty bounty = new Bounty(uuid, items, eco, dateTime);
                bounties.add(bounty);
            }
        } catch (SQLException | IOException e) {
            log.warning("Could not retrieve bounty or binary value could not be converted.");
            return new ArrayList<>();
        }

        return bounties;
    }

    public static Bounty getBounty(UUID uuid) {
        String formattedItems;
        double eco;
        ItemStack[] items;
        Date dateCreated;

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT items, eco, datecreated FROM bounties WHERE uuid = ?;"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next())
                return null;
            formattedItems = resultSet.getString("items");
            eco = resultSet.getDouble("eco");
            dateCreated = resultSet.getDate("dateCreated");
        } catch (SQLException e) {
            log.warning("Could not retrieve bounty. Error: " + e);
            return null;
        }

        try {
            items = TranslateBase64.fromBase64(formattedItems);
        } catch (IOException e) {
            log.warning("Binary value could not be converted.");
            items = null;
        }

        Bounty bounty = new Bounty(uuid, items, eco, dateCreated);

        return bounty;
    }

    public static void removeBounty(UUID uuid) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM bounties WHERE uuid = ?;"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.execute();
        } catch (SQLException e) {
            log.warning("Could not delete database entry. Error: " + e);
        }
    }
}
