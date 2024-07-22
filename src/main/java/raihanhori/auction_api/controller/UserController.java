package raihanhori.auction_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import raihanhori.auction_api.helper.SuccessApiResponse;
import raihanhori.auction_api.response.UserResponse;
import raihanhori.auction_api.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public SuccessApiResponse<UserResponse> show() {
		UserResponse response = userService.getUser();
		
		return SuccessApiResponse.<UserResponse>builder()
				.status(200).data(response)
				.build();
	}
	
}
