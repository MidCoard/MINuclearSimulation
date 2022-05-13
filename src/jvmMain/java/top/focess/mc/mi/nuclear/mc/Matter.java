package top.focess.mc.mi.nuclear.mc;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;
import top.focess.util.serialize.FocessSerializable;

import java.util.Map;

public abstract class Matter implements FocessSerializable {
    private final String namespace;
    private final String name;

    public Matter(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return namespace + ":" + name;
    }

    @Nullable
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("namespace", namespace);
        map.put("name", name);
        return map;
    }
}
