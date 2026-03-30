package org.example.campconnect.Service;

import org.example.campconnect.dto.OptionServiceRequest;
import org.example.campconnect.dto.OptionServiceResponse;

import java.util.List;

public interface IOptionServiceService {
    OptionServiceResponse createOptionService(OptionServiceRequest req);
    OptionServiceResponse updateOptionService(Long id, OptionServiceRequest req);
    void deleteOptionService(Long id);
    OptionServiceResponse getOptionServiceById(Long id);
    List<OptionServiceResponse> getAllOptionServices();
    List<OptionServiceResponse> getByOptionType(String optionType);
    List<OptionServiceResponse> getByVehicleId(Long vehicleId);
}
