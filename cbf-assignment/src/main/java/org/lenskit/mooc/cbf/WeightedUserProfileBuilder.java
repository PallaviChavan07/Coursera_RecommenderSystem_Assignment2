package org.lenskit.mooc.cbf;

import org.lenskit.data.ratings.Rating;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Build a user profile from all positive ratings.
 */
public class WeightedUserProfileBuilder implements UserProfileBuilder {
    /**
     * The tag model, to get item tag vectors.
     */
    private final TFIDFModel model;

    @Inject
    public WeightedUserProfileBuilder(TFIDFModel m) {
        model = m;
    }

    @Override
    public Map<String, Double> makeUserProfile(@Nonnull List<Rating> ratings) {
        // Create a new vector over tags to accumulate the user profile
        Map<String,Double> profile = new HashMap<>();

        // TODO Normalize the user's ratings
        /***
         * To normalize the user's rating, 1st we need to calculate global mean rating.
         * Global mean rating = sum of all ratings / total number of ratings
         */
        double globalMeanRating = 0;
        double sumOfAllRatings = 0;
        for(Rating rating : ratings){
            sumOfAllRatings += rating.getValue();
        }
        globalMeanRating = sumOfAllRatings / ratings.size();

        /***
         * To calculate weighted user profile, use belo formulae:
         * Sum(rating - globalMeanRating) * itemVectorValue
         */
        for(Rating rating : ratings){
            double weight = rating.getValue() - globalMeanRating;
            Map<String, Double> iVector =  model.getItemVector(rating.getItemId());
            for(Map.Entry<String, Double> itemVector : iVector.entrySet()){
                if(!profile.containsKey(itemVector.getKey())){
                    profile.put(itemVector.getKey(), (weight * itemVector.getValue()));
                }else {
                    profile.put(itemVector.getKey(), (profile.get(itemVector.getKey())+weight) * itemVector.getValue());
                }
            }
        }

        // TODO Build the user's weighted profile


        // The profile is accumulated, return it.
        return profile;
    }
}
