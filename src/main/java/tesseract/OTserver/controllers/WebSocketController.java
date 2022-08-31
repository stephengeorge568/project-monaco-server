package tesseract.OTserver.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tesseract.OTserver.objects.StringChangeRequest;

@Controller
@RequestMapping("/")
public class WebSocketController {

    // unused
    @MessageMapping("/string")
    @SendTo("/broker/string-change-request")
    public StringChangeRequest send(StringChangeRequest stringChangeRequest) throws Exception {
        return stringChangeRequest;
    }


}
