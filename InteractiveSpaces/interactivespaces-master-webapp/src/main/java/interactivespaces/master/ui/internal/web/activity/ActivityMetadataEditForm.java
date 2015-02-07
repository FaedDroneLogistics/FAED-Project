/*
 * Copyright (C) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package interactivespaces.master.ui.internal.web.activity;

import interactivespaces.domain.basic.Activity;
import interactivespaces.master.server.services.ActivityRepository;
import interactivespaces.master.ui.internal.web.ConfigurationForm;
import interactivespaces.master.ui.internal.web.MetadataEditFormSupport;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Map;

/**
 * A form for editing activity metadata.
 *
 * @author Keith M. Hughes
 */
@Controller
@RequestMapping("/activity/{id}/metadata/edit")
@SessionAttributes({ "activity", "id", "metadata" })
public class ActivityMetadataEditForm extends MetadataEditFormSupport {

  /**
   * The activity repository.
   */
  private ActivityRepository activityRepository;

  @InitBinder
  public void setAllowedFields(WebDataBinder dataBinder) {
    dataBinder.setDisallowedFields("id");
  }

  @RequestMapping(method = RequestMethod.GET)
  public String setupForm(@PathVariable("id") String id, Model model) {
    Activity activity = activityRepository.getActivityById(id);
    model.addAttribute("activity", activity);
    model.addAttribute("id", id);

    addGlobalModelItems(model);

    ConfigurationForm metadataForm = newMetadataForm(activity.getMetadata());

    model.addAttribute("metadata", metadataForm);

    return "activity/ActivityMetadataEdit";
  }

  @RequestMapping(method = { RequestMethod.PUT, RequestMethod.POST })
  public String processSubmit(@PathVariable("id") String id,
      @ModelAttribute("metadata") ConfigurationForm metadataForm, BindingResult result,
      SessionStatus status) {
    metadataForm.validate(result, false, "space.metadata");
    if (result.hasErrors()) {
      return "activity/ActivityMetadataEdit";
    } else {
      Activity activity = activityRepository.getActivityById(id);

      if (saveMetadataForm(metadataForm, activity)) {
        activityRepository.saveActivity(activity);
      }

      status.setComplete();

      return "redirect:/activity/" + id + "/view.html";
    }
  }

  /**
   * Save the metadata form
   *
   * @param form
   *          the metadata form
   * @param activity
   *          the activity which contains the metadata
   *
   * @return {@code true} if there were changes
   */
  private boolean saveMetadataForm(ConfigurationForm form, Activity activity) {
    Map<String, Object> map = getSubmittedMap(form);

    return saveMetadata(activity, map);
  }

  /**
   * save the metadata.
   *
   * @param activity
   *          the activity being reconfigured
   * @param map
   *          the map of new configurations
   *
   * @return {@code true} if there was a change in the configuration
   */
  private boolean saveMetadata(Activity activity, Map<String, Object> map) {
    Map<String, Object> metadata = activity.getMetadata();
    if (metadata != null) {
      if (metadata.isEmpty() && map.isEmpty()) {
        return false;
      }

      activity.setMetadata(map);

      return true;
    } else {
      // No configuration. If nothing in submission, nothing has changed.
      // Otherwise add everything.
      if (map.isEmpty())
        return false;

      activity.setMetadata(map);

      return true;
    }
  }

  /**
   * @param activityRepository
   *          the activityRepository to set
   */
  public void setActivityRepository(ActivityRepository activityRepository) {
    this.activityRepository = activityRepository;
  }
}