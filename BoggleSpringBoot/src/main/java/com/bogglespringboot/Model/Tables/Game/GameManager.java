
/*
Rest API for games table manager.
It manages every request to the backend by frontend in relation to the table.
 */

@RestController
public class gameManager{

    //hold value of the repository created
    private final gameRepository repository;

    public gameManager (gameRepository repository){
        this.repository = repository;
    }

    @GetMapping("/Game");






}