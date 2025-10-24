package com.example.layout.controller.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.layout.entity.LichTrinh;
import com.example.layout.service.LichTrinhService;

@RestController
@RequestMapping("/api/manager/lichtrinh")
public class UpdateLichTrinhController {
	@Autowired
	private LichTrinhService lichTrinhService;
	  @PutMapping("/{maLichTrinh}")
	  public ResponseEntity<LichTrinh> update(@PathVariable Integer maLichTrinh,
	                                          @RequestBody LichTrinh updatedData)
	  {
	        LichTrinh saved = lichTrinhService.update(maLichTrinh, updatedData); // Cần tạo hàm này trong service
	        return ResponseEntity.ok(saved);		  
	  }
}
