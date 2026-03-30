package org.example.campconnect.Controller;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.Service.IOptionServiceService;
import org.example.campconnect.dto.OptionServiceRequest;
import org.example.campconnect.dto.OptionServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/options")
@RequiredArgsConstructor
public class OptionServiceController {

    private final IOptionServiceService optionServiceService;

    @PostMapping
    public ResponseEntity<OptionServiceResponse> createOptionService(@RequestBody OptionServiceRequest req) {
        return new ResponseEntity<>(optionServiceService.createOptionService(req), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OptionServiceResponse>> getAllOptionServices() {
        return ResponseEntity.ok(optionServiceService.getAllOptionServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionServiceResponse> getOptionServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(optionServiceService.getOptionServiceById(id));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<OptionServiceResponse>> getByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(optionServiceService.getByVehicleId(vehicleId));
    }

    @GetMapping("/type/{optionType}")
    public ResponseEntity<List<OptionServiceResponse>> getByOptionType(@PathVariable String optionType) {
        return ResponseEntity.ok(optionServiceService.getByOptionType(optionType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptionServiceResponse> updateOptionService(
            @PathVariable Long id,
            @RequestBody OptionServiceRequest req) {
        return ResponseEntity.ok(optionServiceService.updateOptionService(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOptionService(@PathVariable Long id) {
        optionServiceService.deleteOptionService(id);
        return ResponseEntity.noContent().build();
    }
}
