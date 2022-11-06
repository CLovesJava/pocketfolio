package com.ssafy.pocketfolio.api.controller;

import com.ssafy.pocketfolio.api.dto.request.PortfolioReq;
import com.ssafy.pocketfolio.api.dto.response.PortfolioRes;
import com.ssafy.pocketfolio.api.service.PortfolioServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/portfolios")
@RequiredArgsConstructor
@Tag(name = "PortfolioController", description = "포트폴리오 API")
public class PortfolioController {

    private final PortfolioServiceImpl portfolioService;

    @PostMapping
    private ResponseEntity<Long> insertPortfolio(HttpServletRequest request, @RequestPart(value="portfolio") PortfolioReq portfolio, @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        log.debug("[POST] Controller - Portfolio");
        Long response = null;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        try{
            long userSeq = (Long) request.getAttribute("userSeq");
            if (userSeq > 0) {
                response = portfolioService.insertPortfolio(portfolio, thumbnail, userSeq, files);
                if (response > 0) {
                    status = HttpStatus.CREATED;
                }
            } else {
                log.error("사용 불가능 토큰");
                status = HttpStatus.FORBIDDEN;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping
    private ResponseEntity<List<PortfolioRes>> findPortfolioList(HttpServletRequest request){
        log.debug("[GET] Controller - findPortfolioList");
        List<PortfolioRes> response = new ArrayList<>();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        try{
            long userSeq = (Long) request.getAttribute("userSeq");
            response = portfolioService.findPortfolioList(userSeq);
            status = HttpStatus.OK;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/{portSeq}")
    private ResponseEntity<PortfolioRes> findPortfolio(@PathVariable(value = "portSeq") long portSeq){
        log.debug("[GET] Controller - findPortfolio");
        PortfolioRes response = null;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        try {
            response = portfolioService.findPortfolio(portSeq);
            status = HttpStatus.OK;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        // TODO: pagenation으로 할 수도 있어서 일단은 전체 개수 따로 반환하지 않음.
        return new ResponseEntity<>(response, status);
    }

    @PatchMapping("/{portSeq}")
    private ResponseEntity<Long> updatePortfolio(@PathVariable(value="portSeq") long portSeq, @RequestPart(value="portfolio") PortfolioReq request, @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        log.debug("[PATCH] Controller - Portfolio");
        // 포트폴리오 저장 후 해당 포트폴리오 번호 반환
        Long response = null;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        try {
            response = portfolioService.updatePortfolio(portSeq, request, thumbnail, files);
            status = HttpStatus.CREATED;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new ResponseEntity<>(response, status);
    }

    @DeleteMapping("/{portSeq}")
    private ResponseEntity<Boolean> deletePortfolio(@PathVariable(value="portSeq") long portSeq) {
        log.debug("[DELETE] Controller - Portfolio");
        boolean response = false;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        try {
            portfolioService.deletePortfolio(portSeq);
            response = true;
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }

        return new ResponseEntity<>(response, status);
    }
}