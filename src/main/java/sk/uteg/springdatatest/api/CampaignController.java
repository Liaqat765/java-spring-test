package sk.uteg.springdatatest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.uteg.springdatatest.api.model.CampaignSummary;
import sk.uteg.springdatatest.api.service.CampaignService;

import java.util.UUID;

@RestController
@RequestMapping("/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/summary/{uuid}")
    public ResponseEntity<CampaignSummary> getSummary(@PathVariable UUID uuid) {
        CampaignSummary campaignSummary = campaignService.getCampaignSummary(uuid);
        return ResponseEntity.ok(campaignSummary);
    }
}
