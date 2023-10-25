package com.kh.product.web;

import com.kh.product.dao.entity.Product;
import com.kh.product.svc.ProductSVC;
import com.kh.product.web.form.AllForm;
import com.kh.product.web.form.DetailForm;
import com.kh.product.web.form.SaveForm;
import com.kh.product.web.form.UpdateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductSVC productSVC;

  //등록양식
  @GetMapping("/add")
  public String addForm(Model model){
    log.info("addForm 호출됨");

    model.addAttribute("saveForm", new SaveForm());
    return "product/add";
  }

  //등록처리
  @PostMapping("/add")
  public String add(
      @Valid @ModelAttribute SaveForm saveForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes
  ){
    log.info("add 호출됨={}",saveForm);

    // 요청데이터 유효성 체크
    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);
      return "product/add";
    }
    if(saveForm.getQuantity() > 1000) {
      bindingResult.rejectValue("quantity","product",new Object[]{1000},null);
    }
    if(saveForm.getQuantity() * saveForm.getPrice() > 10_000_000L) {
      bindingResult.reject("totalPrice",new Object[]{1000},null);
    }
    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);
      return "product/add";
    }

    // 상품등록
    Product product = new Product();
    product.setPname(saveForm.getPname());
    product.setQuantity(saveForm.getQuantity());
    product.setPrice(saveForm.getPrice());
    Long productId = productSVC.save(product);

    log.info("상품아이디={}",productId);
    redirectAttributes.addAttribute("id", productId);

    return "redirect:/products/{id}/detail";
  }

  //조회
  @GetMapping("/{id}/detail")
  public String findById(
      @PathVariable("id") Long id,
      Model model){
    //상품조회
    Optional<Product> findedProduct = productSVC.findById(id);
    Product product = findedProduct.orElseThrow();

    DetailForm detailForm = new DetailForm();
    detailForm.setProductId(product.getProductId());
    detailForm.setPname(product.getPname());
    detailForm.setQuantity(product.getQuantity());
    detailForm.setPrice(product.getPrice());

    model.addAttribute("detailForm",detailForm);
    return "product/detailForm";
  }

  //목록
  @GetMapping
  public String findAll(Model model){
    log.info("findAll()호출됨!");

    //상품목록조회
    List<Product> list = productSVC.findAll();
    List<AllForm> all = new ArrayList<>();
    for(Product product :list){
      AllForm allForm = new AllForm();
      allForm.setProductId(product.getProductId());
      allForm.setPname(product.getPname());
      all.add(allForm);
    }
    model.addAttribute("all",all);

    return "product/all";
  }

  //삭제
  @DeleteMapping("/{id}")
  public String deleteById(@PathVariable("id") Long id){

    int deletedRowCnt = productSVC.deleteById(id);

    return "redirect:/products";
  }

  //수정양식
  @GetMapping("/{id}")
  public String updateForm(
      @PathVariable("id") Long id,
      Model model){
    log.info("updateForm() 호출됨");
    Optional<Product> findedProduct = productSVC.findById(id);

    Product product = findedProduct.orElseThrow();

    UpdateForm updateForm = new UpdateForm();
    updateForm.setProductId(product.getProductId());
    updateForm.setPname(product.getPname());
    updateForm.setQuantity(product.getQuantity());
    updateForm.setPrice(product.getPrice());

    model.addAttribute("updateForm", updateForm);
    return "product/updateForm";
  }

  //수정처리
  @PatchMapping("/{id}")
  public String update(
      @PathVariable("id") Long productId,
      @Valid @ModelAttribute UpdateForm updateForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes){
    log.info("update()호출됨!");
    log.info("updateForm={}",updateForm);

    // 요청데이터 유효성 체크
    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);
      return "product/updateForm";
    }
    if(updateForm.getQuantity() > 2000) {
      bindingResult.rejectValue("quantity","product",new Object[]{2000},null);
    }
    if(updateForm.getQuantity() * updateForm.getPrice() > 20_000_000L) {
      bindingResult.reject("totalPrice",new Object[]{2000},null);
    }
    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);
      return "product/updateForm";
    }

    //상품수정
    Product product = new Product();
    product.setPname(updateForm.getPname());
    product.setQuantity(updateForm.getQuantity());
    product.setPrice(updateForm.getPrice());
    int updatedRow = productSVC.updateById(productId, product);

    //상품조회 리다이렉트
    redirectAttributes.addAttribute("id",productId);
    return "redirect:/products/{id}/detail";
  }
}