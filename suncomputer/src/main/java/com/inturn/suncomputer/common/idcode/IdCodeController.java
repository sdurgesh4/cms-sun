package com.inturn.suncomputer.common.idcode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/id-codes")
public class IdCodeController {
    private final IdCodeService idCodeService;

    public IdCodeController(IdCodeService idCodeService) {
        this.idCodeService = idCodeService;
    }

    @GetMapping("/preview")
    public Map<String, String> preview(@RequestParam String type) {
        return Map.of("code", idCodeService.preview(type));
    }
}
