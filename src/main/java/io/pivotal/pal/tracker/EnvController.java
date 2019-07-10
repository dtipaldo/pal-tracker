package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class EnvController {

    private String port;
    private String memoryLimit;
    private String cfInstanceIndex;
    private String cfInstanceAddr;

    public EnvController(
            @Value("${PORT: NOT SET}") String port,
            @Value("${MEMORY_LIMIT: NOT SET}") String memoryLimit,
            @Value("${CF_INSTANCE_INDEX: NOT SET}") String cfInstanceIndex,
            @Value("${CF_INSTANCE_ADDR: NOT SET}") String cfInstanceAddr
    ) {
        this.port = port;
        this.memoryLimit = memoryLimit;
        this.cfInstanceIndex = cfInstanceIndex;
        this.cfInstanceAddr = cfInstanceAddr;
    }

    @GetMapping("/env")
    public HashMap<String, String> getEnv() {
        HashMap<String, String> map = new HashMap();
        map.put("PORT", this.port);
        map.put("MEMORY_LIMIT", this.memoryLimit);
        map.put("CF_INSTANCE_INDEX", this.cfInstanceIndex);
        map.put("CF_INSTANCE_ADDR", this.cfInstanceAddr);
        return map;
    }
}
