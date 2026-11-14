package com.snowk.blog.api.view;
import org.springframework.web.bind.annotation.*;
@RestController
public class ViewController {
    private final ViewService service; private final ViewSignature signature;
    public ViewController(ViewService service,ViewSignature signature) {this.service=service;this.signature=signature;}
    public record Input(String visitor,long timestamp,String signature) {}
    @GetMapping("/api/public/views/{kind}/{id}")
    public ViewService.State read(@PathVariable String kind,@PathVariable long id) {return service.read(kind,id);}
    @PostMapping("/api/public/views/{kind}/{id}")
    public ViewService.State record(@PathVariable String kind,@PathVariable long id,@RequestBody Input input) {
        return service.record(kind,id,input.visitor(),signature.verify(kind,id,input.visitor(),input.timestamp(),input.signature()));
    }
}
