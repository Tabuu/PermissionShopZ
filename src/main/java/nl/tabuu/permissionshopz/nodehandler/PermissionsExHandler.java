package nl.tabuu.permissionshopz.nodehandler;

import nl.tabuu.permissionshopz.data.node.*;
import nl.tabuu.permissionshopz.nodehandler.exception.NodeHandlerNotFoundException;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.*;

public class PermissionsExHandler implements INodeHandler {

    public PermissionsExHandler() {
        Plugin pexPlugin = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
        if (!(pexPlugin instanceof PermissionsEx))
            throw new NodeHandlerNotFoundException("Could not find PermissionsEx.");
    }

    @Override
    public boolean hasNode(Player player, Node node) {
        PermissionUser user = PermissionsEx.getUser(player);

        if(Objects.isNull(user)) return false;

        switch (node.getType()) {
            case PERMISSION:
            case TEMPORARY_PERMISSION:
                PermissionNode permNode = (PermissionNode) node;
                return user.has(permNode.getPermission(), null);

            case GROUP:
                GroupNode groupNode = (GroupNode) node;
                return user.inGroup(groupNode.getGroupId(), null);

            case TRACK:
                TrackNode trackNode = (TrackNode) node;
                int currentTrack = getUserTrackIndex(user, trackNode.getTrackId());
                return trackNode.getIndex() <= currentTrack;

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
    public boolean addNode(Player player, Node node) {
        PermissionUser user = PermissionsEx.getUser(player);

        if(Objects.isNull(user)) return false;

        switch (node.getType()) {
            case PERMISSION:
                PermissionNode permNode = (PermissionNode) node;
                user.addPermission(permNode.getPermission());
                break;

            case TEMPORARY_PERMISSION:
                TemporaryPermissionNode tempPermNode = (TemporaryPermissionNode) node;
                int timeInSeconds = (int) (tempPermNode.getDuration());
                user.addTimedPermission(tempPermNode.getPermission(), null, timeInSeconds);
                break;

            case GROUP:
                GroupNode groupNode = (GroupNode) node;
                user.addGroup(groupNode.getGroupId());
                break;

            case TRACK:
                TrackNode trackNode = (TrackNode) node;
                removeFromTrack(user, trackNode.getTrackId());
                setUserTrackIndex(user, trackNode.getTrackId(), trackNode.getIndex());
                break;

            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean isNodeTypeSupported(NodeType nodeType) {
        switch (nodeType) {
            case PERMISSION:
            case TEMPORARY_PERMISSION:
            case GROUP:
            case TRACK:
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