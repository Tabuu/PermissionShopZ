package nl.tabuu.permissionshopz.nodehandler;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeBuilder;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.track.Track;
import nl.tabuu.permissionshopz.data.node.*;
import nl.tabuu.permissionshopz.nodehandler.exception.NodeHandlerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LuckPermsHandler implements INodeHandler {

    private final LuckPerms _luckPerms;

    public LuckPermsHandler() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) throw new NodeHandlerNotFoundException("Could not find LuckPerms.");
        else _luckPerms = provider.getProvider();
    }

    @Override
    public boolean hasNode(Player player, nl.tabuu.permissionshopz.data.node.Node node) {
        User user = _luckPerms.getUserManager().getUser(player.getUniqueId());
        NodeType nodeType = node.getType();

        if (Objects.isNull(user)) return false;

        switch (nodeType) {
            case TRACK:
                TrackNode trackNode = (TrackNode) node;

                Track track = _luckPerms.getTrackManager().getTrack(trackNode.getTrackId());
                if (Objects.isNull(track)) return false;

                int currentTrack = getUserTrackIndex(user, track);
                return trackNode.getIndex() <= currentTrack;

            case PERMISSION:
            case TEMPORARY_PERMISSION:
            case GROUP:
                Node luckNode = convertNode(node);
                if (Objects.isNull(luckNode)) return false;
                return user.data().contains(luckNode, NodeEqualityPredicate.IGNORE_VALUE_OR_IF_TEMPORARY).asBoolean();

            default:
                return false;
        }

    }

    public boolean hasNode(User user, Node node) {
        return user.data().contains(node, NodeEqualityPredicate.IGNORE_VALUE_OR_IF_TEMPORARY).asBoolean();
    }

    public boolean hasGroup(User user, String groupId) {
        return hasNode(user, InheritanceNode.builder(groupId).build());
    }

    @Override
    public boolean addNode(Player player, nl.tabuu.permissionshopz.data.node.Node node) {
        User user = _luckPerms.getUserManager().getUser(player.getUniqueId());

        if (Objects.isNull(user)) return false;

        switch (node.getType()) {
            case TRACK:
                TrackNode trackNode = (TrackNode) node;
                Track track = _luckPerms.getTrackManager().getTrack(trackNode.getTrackId());
                if (Objects.isNull(track)) return false;

                removeFromTrack(user, track);
                setUserTrackIndex(user, track, trackNode.getIndex());
                break;

            case PERMISSION:
            case TEMPORARY_PERMISSION:
            case GROUP:
                Node luckNode = convertNode(node);
                if(Objects.isNull(luckNode)) return false;
                user.data().add(luckNode);
                break;

            default:
                return false;
        }

        _luckPerms.getUserManager().saveUser(user);
        return true;
    }

    public int getUserTrackIndex(User user, Track track) {
        List<String> groups = track.getGroups();
        Collection<Node> nodes = user.data().toCollection();

        String foundGroup = null;
        for (Node node : nodes) {
            if (!(node instanceof InheritanceNode)) continue;

            InheritanceNode group = (InheritanceNode) node;
            if (groups.contains(group.getGroupName())) {
                foundGroup = group.getGroupName();
                break;
            }
        }

        if (Objects.nonNull(foundGroup))
            return groups.indexOf(foundGroup);

        return -1;
    }

    public void setUserTrackIndex(User user, Track track, int index) {
        List<String> groups = track.getGroups();
        if (groups.size() <= index) return;

        String targetGroupId = groups.get(index);
        Node node = InheritanceNode.builder(targetGroupId).build();
        user.data().add(node);
    }

    public void removeFromTrack(User user, Track track) {
        List<String> groups = track.getGroups();

        for (String groupId : groups) {
            InheritanceNode node = InheritanceNode.builder(groupId).build();
            user.data().remove(node);
        }
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

    private Node convertNode(nl.tabuu.permissionshopz.data.node.Node node) {
        NodeBuilder<?, ?> nodeBuilder;

        switch (node.getType()) {
            case GROUP:
                GroupNode groupNode = (GroupNode) node;
                nodeBuilder = InheritanceNode.builder(groupNode.getGroupId());
                break;

            case PERMISSION:
                PermissionNode permNode = (PermissionNode) node;
                nodeBuilder = Node.builder(permNode.getPermission());
                break;

            case TEMPORARY_PERMISSION:
                TemporaryPermissionNode tempPermNode = (TemporaryPermissionNode) node;
                nodeBuilder = Node.builder(tempPermNode.getPermission()).expiry(tempPermNode.getDuration(), TimeUnit.MILLISECONDS);
                break;

            default:
                return null;
        }

        return nodeBuilder.build();
    }
}