package sk.uteg.springdatatest.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.uteg.springdatatest.api.model.CampaignSummary;
import sk.uteg.springdatatest.api.model.OptionSummary;
import sk.uteg.springdatatest.api.model.QuestionSummary;
import sk.uteg.springdatatest.db.model.Answer;
import sk.uteg.springdatatest.db.model.Feedback;
import sk.uteg.springdatatest.db.model.Option;
import sk.uteg.springdatatest.db.model.QuestionType;
import sk.uteg.springdatatest.db.repository.FeedbackRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public CampaignSummary getCampaignSummary(UUID campaignId) {
        // Fetch the campaign feedbacks from the repository
        List<Feedback> feedbacks = feedbackRepository.findByCampaign_Id(campaignId);

        // Calculate the total number of feedbacks
        long totalFeedbacks = feedbacks.size();

        // Map feedbacks to QuestionSummaries
        List<QuestionSummary> questionSummaries = feedbacks.stream()
                .flatMap(feedback -> feedback.getAnswers().stream())
                .map(answer -> {
                    QuestionSummary questionSummary = new QuestionSummary();
                    questionSummary.setName(answer.getQuestion().getText());
                    questionSummary.setType(answer.getQuestion().getType());

                    if (answer.getQuestion().getType() == QuestionType.RATING) {
                        // Calculate average rating
                        double ratingAverage = feedbacks.stream()
                                .flatMap(f -> f.getAnswers().stream())
                                .filter(a -> a.getQuestion().equals(answer.getQuestion()))
                                .mapToInt(Answer::getRatingValue)
                                .average()
                                .orElse(0.0);
                        questionSummary.setRatingAverage(BigDecimal.valueOf(ratingAverage));
                    } else if (answer.getQuestion().getType() == QuestionType.CHOICE) {
                        // Calculate option occurrences
                        List<OptionSummary> optionSummaries = answer.getSelectedOptions().stream()
                                .collect(Collectors.groupingBy(Option::getText, Collectors.counting()))
                                .entrySet().stream()
                                .map(entry -> {
                                    OptionSummary optionSummary = new OptionSummary();
                                    optionSummary.setText(entry.getKey());
                                    optionSummary.setOccurrences(entry.getValue().intValue());
                                    return optionSummary;
                                })
                                .collect(Collectors.toList());
                        questionSummary.setOptionSummaries(optionSummaries);
                    }

                    return questionSummary;
                })
                .collect(Collectors.toList());

        // Create and return the CampaignSummary
        CampaignSummary campaignSummary = new CampaignSummary();
        campaignSummary.setTotalFeedbacks(totalFeedbacks);
        campaignSummary.setQuestionSummaries(questionSummaries);

        return campaignSummary;
    }
}
