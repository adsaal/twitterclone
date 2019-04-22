package ademsalih.softwarearch.frontend.controller;

import ademsalih.softwarearch.frontend.model.*;
import ademsalih.softwarearch.frontend.service.FollowService;
import ademsalih.softwarearch.frontend.service.TimeFormatService;
import ademsalih.softwarearch.frontend.service.TweetService;
import ademsalih.softwarearch.frontend.service.UserService;
import ademsalih.softwarearch.frontend.viewmodel.AccountEditUser;
import ademsalih.softwarearch.frontend.viewmodel.OtherEditUser;
import ademsalih.softwarearch.frontend.viewmodel.PasswordEditUser;
import ademsalih.softwarearch.frontend.viewmodel.PictureEditUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class HomeController implements WebMvcConfigurer {

    @Autowired
    TweetService tweetService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    long user_id = 1;

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @GetMapping("/signup")
    public String signup(User user, Model model) {
        return "signup";
    }

    @PostMapping("/register")
    public String register(@Valid User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        user.setUserRole("USER");
        user.setBannerImageName("default-banner-image.jpg");
        user.setProfileImageName("default-profile-image.jpg");
        user.setAccountCreated(Calendar.getInstance().getTime().toInstant().toString());

        userService.registerUser(user);

        return "redirect:/home";
    }

    @GetMapping("/profileaccount")
    public String profileAccount(Model model) {

        User profile = userService.getUserById(user_id);
        model.addAttribute("profile", profile);
        model.addAttribute("settingNumber", 0);
        return "profile-account";
    }

    @PostMapping("/profileaccountsave")
    public String profileAccountSave(@Valid @ModelAttribute AccountEditUser user, BindingResult bindingResult, HttpServletRequest httpServletRequest) {

        if (bindingResult.hasErrors()) {
            String referer = httpServletRequest.getHeader("Referer");
            return "redirect:" + referer;
        }

        User serverUser = userService.getUserById(user_id);

        serverUser.setName(user.getName());
        serverUser.setEmail(user.getEmail());
        serverUser.setUserName(user.getUserName());

        userService.updateUser(serverUser);
        return "redirect:/home";
    }

    @GetMapping("/profilepassword")
    public String profilePassword(Model model) {

        User profile = userService.getUserById(user_id);
        model.addAttribute("profile", profile);
        model.addAttribute("settingNumber", 1);
        return "profile-password";
    }

    @PostMapping("/profilepasswordsave")
    public String profilePasswordSave(@Valid @ModelAttribute PasswordEditUser user, BindingResult bindingResult, HttpServletRequest httpServletRequest) {

        if (bindingResult.hasErrors()) {
            String referer = httpServletRequest.getHeader("Referer");
            return "redirect:" + referer;
        }

        User serverUser = userService.getUserById(user_id);
        serverUser.setPassword(user.getPassword());

        userService.updateUser(serverUser);
        return "redirect:/home";
    }







    @GetMapping("/profilepicture")
    public String profilePicture(Model model) {

        User profile = userService.getUserById(user_id);
        model.addAttribute("profile", profile);

        model.addAttribute("picuser", new PictureEditUser());


        model.addAttribute("settingNumber", 2);
        return "profile-pictures";
    }

    @PostMapping("/profilepicturesave")
    public String profilePictureSave(@ModelAttribute("picuser") PictureEditUser user,
                                     HttpServletRequest httpServletRequest,
                                     @RequestParam(value = "profileImageName", required=false) MultipartFile profileImage,
                                     @RequestParam(value = "bannerImageName", required=false) MultipartFile bannerImage) {

        if (profileImage.isEmpty() && bannerImage.isEmpty()) {
            String referer = httpServletRequest.getHeader("Referer");
            return "redirect:" + referer;
        }

        User serverUser = userService.getUserById(user_id);

        if (!profileImage.isEmpty()) {
            try {
                String IMAGE_LOCATION = "/src/main/upload/static/images/profile/";

                String projectDir = System.getProperty("user.dir");
                String absolutePath = projectDir + IMAGE_LOCATION;

                Path path = Paths.get(absolutePath + profileImage.getOriginalFilename());

                Files.write(path,profileImage.getBytes());

                serverUser.setProfileImageName(user.getProfileImageName().getOriginalFilename());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!bannerImage.isEmpty()) {
            try {
                String IMAGE_LOCATION = "/src/main/upload/static/images/banner/";

                String projectDir = System.getProperty("user.dir");
                String absolutePath = projectDir + IMAGE_LOCATION;

                Path path = Paths.get(absolutePath + bannerImage.getOriginalFilename());

                Files.write(path,bannerImage.getBytes());

                serverUser.setBannerImageName(user.getBannerImageName().getOriginalFilename());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        userService.updateUser(serverUser);
        return "redirect:/home";
    }










    @GetMapping("/profileother")
    public String profileOther(Model model) {

        User profile = userService.getUserById(user_id);
        model.addAttribute("profile", profile);
        model.addAttribute("settingNumber", 3);

        List<String> locations = new ArrayList<>();
        locations.add("Oslo, Norway");
        locations.add("Istanbul, Turkey");
        locations.add("London, UK");
        locations.add("Berlin, Germany");
        locations.add("Dublin, Ireland");
        locations.add("Ankara, Turkey");
        locations.add("Bergen, Norway");

        model.addAttribute("locations", locations);
        return "profile-other";
    }

    @PostMapping("/profileothersave")
    public String profileOtherSave(@Valid @ModelAttribute OtherEditUser user, BindingResult bindingResult, HttpServletRequest httpServletRequest) {

        if (bindingResult.hasErrors()) {
            String referer = httpServletRequest.getHeader("Referer");
            return "redirect:" + referer;
        }

        User serverUser = userService.getUserById(user_id);

        serverUser.setPhone(user.getPhone());
        serverUser.setBio(user.getBio());
        serverUser.setLink(user.getLink());
        serverUser.setLocation(user.getLocation());


        userService.updateUser(serverUser);
        return "redirect:/home";
    }












    @PostMapping("/deleteaccount")
    public String deleteAccount() {
        // userservice.deleteAccount();
        return "redirect:/login";
    }

    @PostMapping("/follow/{id}")
    public String follow(@PathVariable long id, HttpServletRequest request) {

        Follow follow = new Follow();

        follow.setUser(userService.getUserById(user_id));
        follow.setFollowing_user(userService.getUserById(id));

        followService.registerFollow(follow);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("unfollow/{id}")
    public String unfollow(@PathVariable long id, HttpServletRequest request) {

        followService.deleteFollow(user_id, id);

        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/following/{user_id}/{following_id}")
    public String following(@PathVariable("user_id") long user_id, @PathVariable("following_id") long following_id) {

        FollowStatus f = followService.getFollowingStatus(user_id, following_id);

        String s = String.valueOf(f.isFollow());

        return s;
    }

    @GetMapping("/{id}/following")
    public String followings(@PathVariable long id, Model model) {


        model.addAttribute("retweet", new Retweet());
        model.addAttribute("follow", new Follow());

        model.addAttribute("group", 2);

        model.addAttribute("currentUser", user_id);

        // get user information for that user
        User profileUser = userService.getUserById(id);
        profileUser.setAccountCreated(new TimeFormatService().formatJoinDate(profileUser.getAccountCreated()));

        model.addAttribute("profile", profileUser);

        FollowStatus f = followService.getFollowingStatus(user_id, id);

        model.addAttribute("isFollowing", f.isFollow());


        // Get follower count the logged in user
        List<Follow> followers = followService.getFollowersForUserById(id);
        int followerCount = followers.size();
        model.addAttribute("followers", followerCount);

        // Get following count the logged in user
        List<Follow> following = followService.getFollowingsForUserById(id);
        int followingCount = following.size();
        model.addAttribute("followings", followingCount);

        // Get total tweet count the logged in user
        List<Tweet> userTweets = tweetService.getTweetsForUserById(id);
        List<Tweet> userRetweets = tweetService.getRetweetsForUserById(id);
        int totalTweets = userTweets.size() + userRetweets.size();
        model.addAttribute("tweetCount", totalTweets);



        List<Follow> follows = followService.getFollowingsForUserById(id);

        List<FollowPageUser> followingUsers = new ArrayList<>();

        for (Follow follow : follows) {
            long followingId = follow.getFollowing_user().getUser_id();

            User user = userService.getUserById(followingId);

            FollowStatus fs = followService.getFollowingStatus(user_id, user.getUser_id());

            FollowPageUser fpuser = new FollowPageUser(
                    user.getUser_id(),
                    user.getName(),
                    user.getUserName(),
                    user.getProfileImageName(),
                    user.getBannerImageName(),
                    user.getBio(),
                    Boolean.valueOf(fs.isFollow())
            );

            followingUsers.add(fpuser);
        }

        model.addAttribute("followUsers", followingUsers);


        return "profilefollow";
    }

    @GetMapping("/{id}/followers")
    public String followers(@PathVariable long id, Model model) {

        model.addAttribute("group", 3);

        model.addAttribute("retweet", new Retweet());
        model.addAttribute("follow", new Follow());

        model.addAttribute("currentUser", user_id);

        // get user information for that user
        User profileUser = userService.getUserById(id);
        profileUser.setAccountCreated(new TimeFormatService().formatJoinDate(profileUser.getAccountCreated()));
        model.addAttribute("profile", profileUser);

        FollowStatus f = followService.getFollowingStatus(user_id, id);

        model.addAttribute("isFollowing", f.isFollow());


        // Get follower count the logged in user
        List<Follow> followers = followService.getFollowersForUserById(id);
        int followerCount = followers.size();
        model.addAttribute("followers", followerCount);

        // Get following count the logged in user
        List<Follow> following = followService.getFollowingsForUserById(id);
        int followingCount = following.size();
        model.addAttribute("followings", followingCount);

        // Get total tweet count the logged in user
        List<Tweet> userTweets = tweetService.getTweetsForUserById(id);
        List<Tweet> userRetweets = tweetService.getRetweetsForUserById(id);
        int totalTweets = userTweets.size() + userRetweets.size();
        model.addAttribute("tweetCount", totalTweets);



        List<FollowPageUser> followingUsers = new ArrayList<>();

        for (Follow follow : followers) {
            long followingId = follow.getUser().getUser_id();

            User user = userService.getUserById(followingId);

            FollowStatus fs = followService.getFollowingStatus(user_id, user.getUser_id());

            FollowPageUser fpuser = new FollowPageUser(
                    user.getUser_id(),
                    user.getName(),
                    user.getUserName(),
                    user.getProfileImageName(),
                    user.getBannerImageName(),
                    user.getBio(),
                    Boolean.valueOf(fs.isFollow())
            );

            followingUsers.add(fpuser);
        }

        model.addAttribute("followUsers", followingUsers);

        return "profilefollow";
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable long id, Model model) {

        model.addAttribute("retweet", new Retweet());
        model.addAttribute("follow", new Follow());

        model.addAttribute("group", 1);

        // get user information for that user
        User profileUser = userService.getUserById(id);

        profileUser.setAccountCreated(new TimeFormatService().formatJoinDate(profileUser.getAccountCreated()));
        model.addAttribute("profile", profileUser);


        model.addAttribute("currentUser", user_id);

        FollowStatus f = followService.getFollowingStatus(user_id, id);

        model.addAttribute("isFollowing", f.isFollow());


        // Get follower count the logged in user
        List<Follow> followers = followService.getFollowersForUserById(id);
        int followerCount = followers.size();
        model.addAttribute("followers", followerCount);

        // Get following count the logged in user
        List<Follow> following = followService.getFollowingsForUserById(id);
        int followingCount = following.size();
        model.addAttribute("followings", followingCount);

        // Get total tweet count the logged in user
        List<Tweet> userTweets = tweetService.getTweetsForUserById(id);
        List<Tweet> userRetweets = tweetService.getRetweetsForUserById(id);
        int totalTweets = userTweets.size() + userRetweets.size();
        model.addAttribute("tweetCount", totalTweets);




        // get tweets for that user
        List<Tweet> newtweets = tweetService.getTweetsForUserById(id);
        List<Tweet> retweets = tweetService.getRetweetsForUserById(id);

        List<Tweet> tweets = new ArrayList<>();

        tweets.addAll(newtweets);
        tweets.addAll(retweets);

        Collections.sort(tweets, Comparator.comparing(Tweet::getDateTime));
        Collections.reverse(tweets);

        List<UserTweet> feedTweets = new ArrayList<>();

        for (Tweet tweet : tweets) {

            User user = userService.getUserById(tweet.getUser_id());

            UserTweet userTweet = new UserTweet(
                    user.getUser_id(),
                    tweet.getId(),
                    user.getName(),
                    user.getUserName(),
                    user.getProfileImageName(),
                    new TimeFormatService().formatTimeAgo(tweet.getDateTime()),
                    tweet.getImageName(),
                    tweet.getMessage()
            );

            if (tweet.getNewTweet() != null) {

                User retweeter = userService.getUserById(tweet.getNewTweet().getUser_id());

                UserTweet retweet = new UserTweet(
                        retweeter.getUser_id(),
                        tweet.getNewTweet().getId(),
                        retweeter.getName(),
                        retweeter.getUserName(),
                        retweeter.getProfileImageName(),
                        new TimeFormatService().formatTimeAgo(tweet.getNewTweet().getDateTime()),
                        tweet.getNewTweet().getImageName(),
                        tweet.getNewTweet().getMessage()
                );

                userTweet.setNewTweet(retweet);
            }

            feedTweets.add(userTweet);
        }

        model.addAttribute("feedTweets", feedTweets);


        return "profiletweets";
    }

    // TODO: ADD PRINCIPAL FOR USER ID

    @PostMapping("/tweet")
    public String tweet(@ModelAttribute("tweet") Tweet tweet, @RequestParam(value = "file", required=false) MultipartFile file, HttpServletRequest request) {

        tweet.setUser_id(user_id);
        tweet.setNewTweet(null);
        tweet.setDateTime(Calendar.getInstance());


        if (!file.isEmpty()) {
            try {
                String IMAGE_LOCATION = "/src/main/upload/static/images/tweet/";

                String projectDir = System.getProperty("user.dir");
                String absolutePath = projectDir + IMAGE_LOCATION;

                Path path = Paths.get(absolutePath + file.getOriginalFilename());

                Files.write(path,file.getBytes());

                tweet.setImageName(file.getOriginalFilename());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tweetService.postTweet(tweet);

        return "redirect:/home";
    }

    @PostMapping("/retweet/{id}")
    public String retweet(@ModelAttribute("retweet") Retweet retweet, @PathVariable("id") long id, HttpServletRequest request) {

        retweet.setDateTime(Calendar.getInstance());
        retweet.setUser_id(user_id);

        Tweet tweet = new Tweet();
        tweet.setId(id);
        retweet.setNewTweet(tweet);

        tweetService.postRetweet(retweet);

        String referer = request.getHeader("Referer");

        return "redirect:" + referer;
    }

    @GetMapping("/deleteTweet/{id}")
    public String deleteTweet(@PathVariable long id, HttpServletRequest request) {
        tweetService.deleteTweet(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/deleteRetweet/{id}")
    public String deleteRetweet(@PathVariable long id, HttpServletRequest request) {
        tweetService.deleteRetweet(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/")
    public String feed(Model model) {

        List<Tweet> tweetsList = tweetService.getFeed();

        Collections.sort(tweetsList, Comparator.comparing(Tweet::getDateTime));
        Collections.reverse(tweetsList);

        List<UserTweet> feedTweets = new ArrayList<>();

        for (Tweet tweet : tweetsList) {

            User user = userService.getUserById(tweet.getUser_id());

            UserTweet userTweet = new UserTweet(
                    user.getUser_id(),
                    tweet.getId(),
                    user.getName(),
                    user.getUserName(),
                    user.getProfileImageName(),
                    new TimeFormatService().formatTimeAgo(tweet.getDateTime()),
                    tweet.getImageName(),
                    tweet.getMessage()
            );

            if (tweet.getNewTweet() != null) {

                User retweeter = userService.getUserById(tweet.getNewTweet().getUser_id());

                UserTweet retweet = new UserTweet(
                        retweeter.getUser_id(),
                        tweet.getNewTweet().getId(),
                        retweeter.getName(),
                        retweeter.getUserName(),
                        retweeter.getProfileImageName(),
                        new TimeFormatService().formatTimeAgo(tweet.getNewTweet().getDateTime()),
                        tweet.getNewTweet().getImageName(),
                        tweet.getNewTweet().getMessage()
                );

                userTweet.setNewTweet(retweet);
            }

            feedTweets.add(userTweet);
        }

        model.addAttribute("feedTweets", feedTweets);

        return "frontpage";
    }

    @GetMapping("/home")
    public String home(Model model) {

        User loggedInUser = userService.getUserById(user_id);
        model.addAttribute("profile", loggedInUser);

        model.addAttribute("currentUser", user_id);

        // Models for tweeting and retweeting
        model.addAttribute("tweet", new Tweet());
        model.addAttribute("retweet", new Retweet());

        // Get follower count the logged in user
        List<Follow> followers = followService.getFollowersForUserById(user_id);
        int followerCount = followers.size();
        model.addAttribute("followerCount", followerCount);

        // Get following count the logged in user
        List<Follow> followings = followService.getFollowingsForUserById(user_id);
        int followingCount = followings.size();
        model.addAttribute("followingCount", followingCount);

        // Get total tweet count the logged in user
        List<Tweet> userTweets = tweetService.getTweetsForUserById(user_id);
        List<Tweet> userRetweets = tweetService.getRetweetsForUserById(user_id);
        int totalTweets = userTweets.size() + userRetweets.size();
        model.addAttribute("totalTweets", totalTweets);




        List<Tweet> tweetsList = new ArrayList<>();

        for (Follow f : followings) {

            List<Tweet> tweets = tweetService.getTweetsForUserById(f.getFollowing_user().getUser_id());
            List<Tweet> retweets = tweetService.getRetweetsForUserById(f.getFollowing_user().getUser_id());

            tweetsList.addAll(tweets);
            tweetsList.addAll(retweets);
        }

        tweetsList.addAll(userTweets);
        tweetsList.addAll(userRetweets);


        Collections.sort(tweetsList, Comparator.comparing(Tweet::getDateTime));
        Collections.reverse(tweetsList);

        List<UserTweet> feedTweets = new ArrayList<>();

        for (Tweet tweet : tweetsList) {

            User user = userService.getUserById(tweet.getUser_id());

            UserTweet userTweet = new UserTweet(
                    user.getUser_id(),
                    tweet.getId(),
                    user.getName(),
                    user.getUserName(),
                    user.getProfileImageName(),
                    new TimeFormatService().formatTimeAgo(tweet.getDateTime()),
                    tweet.getImageName(),
                    tweet.getMessage()
            );

            if (tweet.getNewTweet() != null) {

                User retweeter = userService.getUserById(tweet.getNewTweet().getUser_id());

                UserTweet retweet = new UserTweet(
                        retweeter.getUser_id(),
                        tweet.getNewTweet().getId(),
                        retweeter.getName(),
                        retweeter.getUserName(),
                        retweeter.getProfileImageName(),
                        new TimeFormatService().formatTimeAgo(tweet.getNewTweet().getDateTime()),
                        tweet.getNewTweet().getImageName(),
                        tweet.getNewTweet().getMessage()
                );

                userTweet.setNewTweet(retweet);
            }

            feedTweets.add(userTweet);
        }

        model.addAttribute("feedTweets", feedTweets);
        return "home";
    }

}
