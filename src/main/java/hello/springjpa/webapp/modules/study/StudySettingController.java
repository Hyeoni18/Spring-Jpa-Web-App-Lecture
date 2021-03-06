package hello.springjpa.webapp.modules.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.springjpa.webapp.modules.account.Account;
import hello.springjpa.webapp.modules.account.CurrentUser;
import hello.springjpa.webapp.modules.account.form.ZoneForm;
import hello.springjpa.webapp.modules.study.form.StudyDescriptionForm;
import hello.springjpa.webapp.modules.tag.Tag;
import hello.springjpa.webapp.modules.tag.TagForm;
import hello.springjpa.webapp.modules.tag.TagRepository;
import hello.springjpa.webapp.modules.tag.TagService;
import hello.springjpa.webapp.modules.zone.Zone;
import hello.springjpa.webapp.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;

    @GetMapping("/description")
    public String viewStudySetting(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
        return "study/settings/description";
    }
    
    @PostMapping("/description")
    public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path
                                    , @Valid StudyDescriptionForm studyDescriptionForm, Errors errors
                                    , Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path); //persist ??????, ???????????? ????????? ???????????????.

        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/settings/description";
        }
        
        studyService.updateStudyDescription(study, studyDescriptionForm);
        attributes.addFlashAttribute("message", "????????? ????????? ??????????????????.");
        return "redirect:/study/"+study.getEncodedPath()+"/settings/description";
    }

    @GetMapping("/banner")
    public String studyImageForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/banner";
    }

    @PostMapping("/banner")
    public String studyImageSubmit(@CurrentUser Account account, @PathVariable String path, String image
                                    , RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyImage(study, image);
        attributes.addFlashAttribute("message", "????????? ???????????? ??????????????????.");
        return "redirect:/study/"+study.getEncodedPath()+"/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.enableStudyBanner(study);
        return "redirect:/study/"+study.getEncodedPath()+"/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.disableStudyBanner(study);
        return "redirect:/study/"+study.getEncodedPath()+"/settings/banner";
    }

    @GetMapping("/tags")
    public String studyTagForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("tags", study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
        return "study/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        studyService.addTag(study, tag); //????????? study, tag ?????? repository ?????? ??????????????? ???????????? ?????? ?????????. persist. ???????????? repository ??? ?????? ????????? ????????? ????????????.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeTag(study, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String studyZonesForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("zones", study.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "study/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if(zone == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.addZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String studySettingForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/study";
    }

    @PostMapping("/study/publish")
    public String publishStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.publish(study);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:/study/"+study.getEncodedPath()+"/settings/study";
    }

    @PostMapping("/study/close")
    public String closeStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.close(study);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:/study/"+study.getEncodedPath()+"/settings/study";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentUser Account account, @PathVariable String path, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if(!study.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1?????? ?????? ?????? ?????? ????????? ????????? ????????? ??? ????????????.");
            return "redirect:/study/"+study.getEncodedPath()+"/settings/study";
        }
        studyService.startRecruit(study);
        attributes.addFlashAttribute("message","?????? ????????? ???????????????.");
        return "redirect:/study/"+study.getEncodedPath()+"/settings/study";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentUser Account account, @PathVariable String path, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        if(!study.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1?????? ?????? ?????? ?????? ????????? ????????? ????????? ??? ????????????.");
            return "redirect:/study/"+study.getEncodedPath()+"/settings/study";
        }
        studyService.stopRecruit(study);
        attributes.addFlashAttribute("message", "?????? ????????? ???????????????.");
        return "redirect:/study/"+study.getEncodedPath()+"/settings/study";
    }

    @PostMapping("/study/path")
    public String updateStudyPath(@CurrentUser Account account, @PathVariable String path, @RequestParam String newPath
                                    ,Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        //????????? validator??? Form object ????????? ????????? ?????? ?????? ??????
        if(!studyService.isValidPath(newPath)) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute("studyPathError","?????? ????????? ????????? ????????? ??? ????????????. ?????? ?????? ???????????????.");
            return "study/settings/study";
        }

        studyService.updateStudyPath(study, newPath);
        attributes.addFlashAttribute("message", "????????? ????????? ??????????????????.");
        return "redirect:/study/"+study.getEncodedPath()+"/settings/study";
    }

    @PostMapping("/study/title")
    public String updateStudyTitle(@CurrentUser Account account, @PathVariable String path, @RequestParam String newTitle
                                    ,Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if(!studyService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(path);
            model.addAttribute("studyTitleError", "????????? ????????? ?????? ???????????????.");
            return "study/settings/study";
        }

        studyService.updateStudyTitle(study, newTitle);
        attributes.addFlashAttribute("message", "????????? ????????? ??????????????????.");
        return "redirect:/study/"+study.getEncodedPath()+"/settings/study";
    }

    @PostMapping("/study/remove")
    public String removeStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        //?????? ??????????????? ?????? ?????? ?????? ??? (????????? ?????????) ????????? ????????? ????????? ????????? ?????? ?????? ???. ????????? ????????? ?????? ???????????????.
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.remove(study);
        return "redirect:/";
    }
}
