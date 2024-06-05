package com.tr.encrypt.decrypt.api.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UuidKit {

    /**
     * 返回一个 uuid
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 返回多个 uuid
     */
    public static List<String> getUuids(Integer number) {
        List<String> list = new ArrayList<>();
        while (0 <= (number--)) {
            list.add(getUuid());
        }
        return list;
    }

}
