package nl.tabuu.permissionshopz.permissionhandler;

import nl.tabuu.permissionshopz.PermissionShopZ;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum NodeType {
    PERMISSION("p", "perm"),
    TEMPORARY_PERMISSION("tp", "tempperm"),
    GROUP("g"),
    TRACK("t"),
    UNKNOWN;

    private static final Pattern NODE_PATTERN, ARG_PATTERN;

    static {
        NODE_PATTERN = Pattern.compile("^(?<type>[^$]+)\\$(?<value>[^$]+)(?:\\$(?<args>.+))?$");
        ARG_PATTERN = Pattern.compile("(?<arg>[^,]+),?");
    }

    private final String[] _aliases;

    NodeType(String... aliases) {
        _aliases = aliases;
    }

    public boolean isAlias(String string) {
        for(String alias : _aliases)
            if(alias.equalsIgnoreCase(string))
                return true;

        return name().equalsIgnoreCase(string);
    }

    public String toString(String node) {
        String value = getValue(node);
        String[] arguments = getArguments(node);

        Object[] replacements = new Object[arguments.length * 2 + 2];

        for(int i = 0; i < arguments.length; i++) {
            replacements[i * 2] = String.format("{ARGS_%d}", i);
            replacements[i * 2 + 1] = arguments[i];
        }

        replacements[replacements.length - 2] = "{VALUE}";
        replacements[replacements.length - 1] = value;

        return PermissionShopZ.getInstance().getLocal().translate(
                String.format("NODE_TYPE_TO_STRING_%s", name()),replacements);
    }

    public String getValue(String node) {
        Matcher nodeMatcher = NODE_PATTERN.matcher(node);
        if(!nodeMatcher.find()) return null;

        return nodeMatcher.group("value");
    }

    public String[] getArguments(String node) {
        Matcher nodeMatcher = NODE_PATTERN.matcher(node);
        if(!nodeMatcher.find()) return new String[0];

        String arguments = nodeMatcher.group("args");
        if(Objects.isNull(arguments)) return new String[0];

        List<String> argumentList = new ArrayList<>();
        Matcher argumentMatcher = ARG_PATTERN.matcher(arguments);
        while (argumentMatcher.find())
            argumentList.add(argumentMatcher.group("arg"));

        return argumentList.toArray(new String[0]);
    }

    public static NodeType fromNode(String node) {
        Matcher nodeMatcher = NODE_PATTERN.matcher(node);
        if(!nodeMatcher.find()) return UNKNOWN;

        String type = nodeMatcher.group("type");

        for(NodeType nodeType : values())
            if(nodeType.isAlias(type))
                return nodeType;

        return UNKNOWN;
    }
}
