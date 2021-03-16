package nl.tabuu.permissionshopz.permissionhandler;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeBuilder;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.track.Track;
import nl.tabuu.permissionshopz.permissionhandler.exception.PermissionHandlerNotFoundException;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LuckPermsHandler implements IPermissionHandler {

    private final LuckPerms _luckPerms;

    public LuckPermsHandler(){
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) throw new PermissionHandlerNotFoundException("Could not find LuckPerms.");
        else _luckPerms = provider.getProvider();
    }

    @Override
    public boolean hasNode(Player player, NodeType nodeType, String stringNode) {
        User user = _luckPerms.getUserManager().getUser(player.getUniqueId());

        if(Objects.isNull(user)) return false;

        switch (nodeType) {
            case TRACK:
                String value = nodeType.getValue(stringNode);
                String[] arguments = nodeType.getArguments(stringNode);
                if(Objects.isNull(value) || arguments.length < 1) return false;

                Track track = _luckPerms.getTrackManager().getTrack(value);
                if(Objects.isNull(track)) return false;

                int trackIndex;
                try {
                    trackIndex = Integer.parseInt(arguments[0]);
                } catch (NumberFormatException exception) { return false; }

                int currentTrack = getUserTrackIndex(user, track);
                return trackIndex <= currentTrack;

            case PERMISSION:
            case TEMPORARY_PERMISSION:
            case GROUP:
                Node node = nodeFromString(nodeType, stringNode);
                if(Objects.isNull(node)) return false;
                return user.data().contains(node, NodeEqualityPredicate.IGNORE_VALUE_OR_IF_TEMPORARY).asBoolean();

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
    public void addNode(Player player, NodeType nodeType, String stringNode) {
        String value = nodeType.getValue(stringNode);
        String[] arguments = nodeType.getArguments(stringNode);
        User user = _luckPerms.getUserManager().getUser(player.getUniqueId());

        if(Objects.isNull(value) || Objects.isNull(user)) return;

        switch (nodeType) {
            case TRACK:
                Track track = _luckPerms.getTrackManager().getTrack(value);
                if(Objects.isNull(track)) return;

                int trackIndex;
                try {
                    trackIndex = Integer.parseInt(arguments[0]);
                } catch (NumberFormatException exception) { return; }

                removeFromTrack(user, track);
                setUserTrackIndex(user, track, trackIndex);
                break;

            case PERMISSION:
            case TEMPORARY_PERMISSION:
            case GROUP:
                Node node = nodeFromString(nodeType, stringNode);
                if(Objects.isNull(node)) return;

                user.data().add(node);
                break;

            default:
                return;
        }

        _luckPerms.getUserManager().saveUser(user);
    }

    public int getUserTrackIndex(User user, Track track) {
        List<String> groups = track.getGroups();
        Collection<Node> nodes = user.data().toCollection();

        String foundGroup = null;
        for(Node node : nodes) {
            if(!(node instanceof InheritanceNode)) continue;

            InheritanceNode group = (InheritanceNode) node;
            if(groups.contains(group.getGroupName())) {
                foundGroup = group.getGroupName();
                break;
            }
        }

        if(Objects.nonNull(foundGroup))
            return groups.indexOf(foundGroup);

        return -1;
    }

    public void setUserTrackIndex(User user, Track track, int index) {
        List<String> groups = track.getGroups();
        if(groups.size() <= index) return;

        String targetGroupId = groups.get(index);
        Node node = InheritanceNode.builder(targetGroupId).build();
        user.data().add(node);
    }

    public void removeFromTrack(User user, Track track) {
        List<String> groups = track.getGroups();

        for(String groupId : groups) {
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

    private Node nodeFromString(NodeType nodeType, String node) {
        String value = nodeType.getValue(node);
        String[] arguments = nodeType.getArguments(node);

        if(Objects.isNull(value)) return null;

        NodeBuilder<?,?> nodeBuilder;

        switch (nodeType) {
            case GROUP:
                nodeBuilder = InheritanceNode.builder(value);
                break;

            case PERMISSION:
            case TEMPORARY_PERMISSION:
                nodeBuilder = Node.builder(value);
                break;

            default:
                return null;
        }

        if(arguments.length > 0) {
            long time = Serializer.TIME.deserialize(arguments[0]);
            nodeBuilder.expiry(time, TimeUnit.MILLISECONDS).build();
        }

        return nodeBuilder.build();
    }
}