package net.idt.geolocation.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/app")
public class GreetingController {

  @RequestMapping(value = "/greeting", method = RequestMethod.GET)
  public String greeting(Model model) {
    return "greeting";
  }

  @ResponseBody
  @RequestMapping(value = "/greeting1", method = RequestMethod.GET)
  public String greeting1() {
    return "asdjfadskf";
  }
}
