package com.budgeteer.api.controller;

import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.dto.category.CategoryListDto;
import com.budgeteer.api.dto.category.SingleCategoryDto;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.CategoryRepository;
import com.budgeteer.api.repository.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class CategoryControllerTest {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    @Client(value = "/${api.base-path}", errorType = ErrorResponse.class)
    RxHttpClient client;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    public void setup() {
        testCategory = TestUtils.createTestCategory();
        testUser = userRepository.save(TestUtils.createTestUser());
        testCategory.setUser(testUser);
        categoryRepository.save(testCategory);
    }

    @Test
    public void shouldReturnListOfOneCategory() {
        HttpResponse<CategoryListDto> response = client.toBlocking()
                .exchange(HttpRequest.GET("/categories?userId=" + testUser.getId()), CategoryListDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnSingleCategory() {
        HttpResponse<SingleCategoryDto> response = client.toBlocking()
                .exchange(HttpRequest.GET("/categories/" + testCategory.getId()), SingleCategoryDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals("Example Category", response.getBody().get().getName());
    }

    @Test
    public void shouldReturnNotFound() {
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.GET("/categories/123456789"), SingleCategoryDto.class));
        assertEquals(HttpStatus.NOT_FOUND, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("NOT_FOUND", errorResponse.getCode());
    }

    @Test
    public void shouldCreateCategoryWithoutParent() {
        SingleCategoryDto dto = new SingleCategoryDto();
        dto.setName("new category");
        dto.setUserId(testUser.getId());
        HttpResponse<SingleCategoryDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/categories", dto), SingleCategoryDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        SingleCategoryDto responseDto = response.getBody().get();
        assertEquals(dto.getName(), responseDto.getName());
        assertEquals(dto.getUserId(), responseDto.getUserId());
        assertEquals(BigDecimal.ZERO, responseDto.getBudgeted());
        assertNull(responseDto.getParentId());
    }

    @Test
    public void shouldCreateCategoryWithParent() {
        SingleCategoryDto dto = new SingleCategoryDto();
        dto.setName("cat with parent");
        dto.setUserId(testUser.getId());
        dto.setParentId(testCategory.getId());
        HttpResponse<SingleCategoryDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/categories", dto), SingleCategoryDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        SingleCategoryDto responseDto = response.getBody().get();
        assertEquals(dto.getName(), responseDto.getName());
        assertEquals(dto.getUserId(), responseDto.getUserId());
        assertEquals(BigDecimal.ZERO, responseDto.getBudgeted());
        assertEquals(testCategory.getId(), responseDto.getParentId());
    }

    @Test
    public void shouldUpdateCategory() {
        SingleCategoryDto dto = new SingleCategoryDto();
        dto.setName("cat with parent");
        dto.setUserId(testUser.getId());
        HttpResponse<SingleCategoryDto> response = client.toBlocking()
                .exchange(HttpRequest.PUT("/categories/" + testCategory.getId(), dto), SingleCategoryDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        SingleCategoryDto responseDto = response.getBody().get();
        assertEquals(dto.getName(), responseDto.getName());
        assertEquals(dto.getUserId(), responseDto.getUserId());
        assertEquals(BigDecimal.ZERO, responseDto.getBudgeted());
    }

    @Test
    public void shouldDeleteCategory() {
        HttpResponse<SingleCategoryDto> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/categories/" + testCategory.getId()), SingleCategoryDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        List<Category> categoryList = categoryRepository.findAll();
        assertEquals(0, categoryList.size());
    }

    @Test
    public void shouldFailWhenNoNameGiven() {
        SingleCategoryDto dto = new SingleCategoryDto();
        dto.setName("");
        dto.setUserId(testCategory.getId());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/categories", dto), SingleCategoryDto.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_CATEGORY_NAME", errorResponse.getCode());
    }
}