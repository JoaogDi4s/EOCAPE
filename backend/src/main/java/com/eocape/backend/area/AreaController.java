package com.eocape.backend.area;

import com.eocape.backend.area.dto.AreaRequest;
import com.eocape.backend.area.dto.AreaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/area")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService service;

    @PostMapping
    public ResponseEntity<AreaResponse> criar(@Valid @RequestBody AreaRequest request) {
        AreaResponse salvo = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<AreaResponse>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public AreaResponse buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }

}