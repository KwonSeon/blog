package com.snowk.blog.api.settings;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController @RequiredArgsConstructor
public class BlogSettingsController {
 private final BlogSettingsService service;
 @GetMapping({"/api/public/site-settings","/api/admin/site-settings"}) public SettingsView read(){return service.read();}
 @PutMapping("/api/admin/site-settings") public SettingsView save(@Valid @RequestBody SettingsView view){return service.save(view);}
}
