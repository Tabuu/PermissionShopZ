package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.permissionhandler.exception.PermissionHandlerNotFoundException;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import ru.tehkode.permissions.events.PermissionEntityEvent;
import ru.tehkode.permissions.exceptions.RankingException;

import java.util.*;

public class PermissionsExHandler implements IPermissionHandler {

    public PermissionsExHandler() {
        Plugin pexPlugin = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
        if (!(pexPlugin instanceof PermissionsEx))
            throw new PermissionHandlerNotFoundException("Could not find PermissionsEx.");
    }

    @Override
    public boolean hasNode(Player player, NodeType nodeType, String node) {
        String value = nodeType.getValue(node);
        PermissionUser user = PermissionsEx.getUser(player);

        if(Objects.isNull(value) || Objects.isNull(user)) return false;

        switch (nodeType) {
            case PERMISSION:
            case TEMPORARY_PERMISSION:
                return user.has(value, null);

            case GROUP:
                return user.inGroup(value, null);

            case TRACK:
                String[] arguments = nodeType.getArguments(node);
                if(arguments.length < 1) return false;

                int trackIndex;
                try {
                    trackIndex = Integer.parseInt(arguments[0]);
                } catch (NumberFormatException exception) { return false; }

                int currentTrack = getUserTrackIndex(user, value);
                return trackIndex <= currentTrack;

            default:
                return false;
        }
    }

    public void removeFromTrack(PermissionUser user, String trackId) {
        List<PermissionGroup> track = getTrack(trackId);
        for(PermissionGroup group : track) {
            user.removeGroup(group);
        }
    }

    @Override
    public void addNode(Player player, NodeType nodeType, String node) {
        String value = nodeType.getValue(node);
        String[] arguments = nodeType.getArguments(node);
        PermissionUser user = PermissionsEx.getUser(player);

        if(Objects.isNull(value) || Objects.isNull(user)) return;

        switch (nodeType) {
            case PERMISSION:
                user.addPermission(value);
                break;

            case TEMPORARY_PERMISSION:
                if(arguments.length < 1) return;
                long lifeTime = Serializer.TIME.deserialize(arguments[0]);
                int timeInSeconds = (int) (lifeTime / 1000L);
                user.addTimedPermission(value, null, timeInSeconds);
                break;

            case GROUP:
                user.addGroup(value);
                break;

            case TRACK:
                int trackIndex;
                try {
                    trackIndex = Integer.parseInt(arguments[0]);
                } catch (NumberFormatException exception) { return; }

                removeFromTrack(user, value);
                setUserTrackIndex(user, value, trackIndex);
                break;
        }
    }

    @Override
    public boolean isNodeTypeSupported(NodeType nodeType) {
        switch (nodeType) {
            case PERMISSION:
            case TEMPORARY_PERMISSION:
            case GROUP:
                return true;

            default:
                return false;
        }
    }

    public void setUserTrackIndex(PermissionUser user, String trackId, int index) {
        List<PermissionGroup> track = getTrack(trackId);
        if(track.size() <= index) return;

        PermissionGroup target = track.get(index);
        user.addGroup(target);
    }

    public int getUserTrackIndex(PermissionUser user, String trackId) {
        PermissionGroup current = user.getRankLadderGroup(trackId);
        return getTrack(trackId).indexOf(current);
    }

    public List<PermissionGroup> getTrack(String trackId) {
        NavigableMap<Integer, PermissionGroup> track = new TreeMap<>(PermissionsEx.getPermissionManager().getRankLadder(trackId));
        return new LinkedList<>(track.values());
    }
}