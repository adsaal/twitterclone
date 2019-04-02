package ademsalih.softwarearch.userservice;

import ademsalih.softwarearch.userservice.model.Follow;
import ademsalih.softwarearch.userservice.model.User;
import ademsalih.softwarearch.userservice.model.UserRoles;
import ademsalih.softwarearch.userservice.repository.FollowRepository;
import ademsalih.softwarearch.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserserviceApplication implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserserviceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAllInBatch();
        followRepository.deleteAllInBatch();

        User adem = new User(
                "Adem",
                "Salih",
                "adem@gmail.com",
                "93689146",
                "adem123",
                "test",
                "26.03.2019",
                "imageName.jpg",
                UserRoles.USER.name());

        User pinar = new User(
                "Pinar",
                "Salih",
                "pinar@gmail.com",
                "93689144",
                "pinarsalihgs",
                "test",
                "26.03.2019",
                "imageName2.jpg",
                UserRoles.USER.name());

        User esra = new User(
                "Esra",
                "Salih",
                "pinar@gmail.com",
                "93897123",
                "esra123",
                "esraerkul",
                "26.03.2019",
                "esra.jpg",
                UserRoles.USER.name());

        userRepository.save(adem);
        userRepository.save(pinar);
        userRepository.save(esra);

        Follow follow = new Follow(adem, pinar);
        Follow follow2 = new Follow(adem, esra);
        Follow follow3 = new Follow(pinar, esra);

        followRepository.save(follow);
        followRepository.save(follow2);
        followRepository.save(follow3);


    }

}