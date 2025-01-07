package com.client_service.controller;

import com.client_service.entity.Client;
import com.client_service.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.EnumUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/v1/clients")
public class ClientsController {

    private final ClientRepository clientRepository;

    private static final Logger logger= LoggerFactory.getLogger(ClientsController.class);

    public ClientsController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    public List<Client> getAllClients(){
        return clientRepository.findAll();
    }
    @GetMapping("/byPaging")
    public Page<Client> getClients(@RequestParam(defaultValue = "0") Integer pageNumber,
                                   @RequestParam(defaultValue = "5") Integer pageSize,
                                   @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable;
        if (sort!= null){
            pageable= PageRequest.of(pageNumber,pageSize, Sort.Direction.ASC, sort);
        }else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }
        return clientRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Client getClient(@PathVariable Long id) {
        logger.info("Begin getClient()");
        return clientRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody Client client) throws URISyntaxException {
        logger.info("Begin createClient()");
        Client savedClient = clientRepository.save(client);
        return ResponseEntity.created(new URI("/client/" + savedClient.getId())).body(savedClient);
    }

    @PostMapping("/addAll")
    public ResponseEntity<?> saveAll(@RequestBody List<Client> clients) throws URISyntaxException {
        logger.info("Begin saveAll()");
        List<Client> savedClients = clientRepository.saveAll(clients);
        return ResponseEntity.created(new URI("/clients/" + savedClients.stream().iterator())).body(savedClients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @RequestBody Client client) {
        logger.info("Begin updateClient()");
        Client currentClient = clientRepository.findById(id).orElseThrow(RuntimeException::new);
        currentClient.setId(id);
        currentClient.setName(client.getName());
        currentClient.setDepartment(client.getDepartment());
        currentClient = clientRepository.save(client);

        return ResponseEntity.ok(currentClient);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        logger.info("Begin deleteClient()");
        clientRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
