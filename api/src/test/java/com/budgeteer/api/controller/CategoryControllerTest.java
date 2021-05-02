package com.budgeteer.api.controller;

import com.budgeteer.api.base.AuthenticationExtension;
import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.dto.account.SingleAccountDto;
import com.budgeteer.api.dto.category.CategoryListDto;
import com.budgeteer.api.dto.category.SingleCategoryDto;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.CategoryRepository;
import com.budgeteer.api.repository.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
@Tag("Integration")
public class CategoryControllerTest {

    @RegisterExtension
    AuthenticationExtension authExtension = new AuthenticationExtension();

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    @Client(value = "/${api.base-path}", errorType = ErrorResponse.class)
    RxHttpClient client;

    private User testUser;
    private Category testCategory;
    private Category additionalCategory;

    @BeforeEach
    public void setup() {
        testCategory = TestUtils.createTestCategory();
        additionalCategory = TestUtils.createTestCategory();
        testUser = userRepository.save(TestUtils.createSecureTestUser());
        User secondUser = userRepository.save(TestUtils.createAdditionalTestUser());
        testCategory.setUser(testUser);
        testCategory = categoryRepository.save(testCategory);
        additionalCategory.setUser(secondUser);
        additionalCategory.setParent(testCategory);
        additionalCategory = categoryRepository.save(additionalCategory);
    }

    @Test
    public void shouldReturnListOfOneCategory() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/categories?userId=" + testUser.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<CategoryListDto> response = client.toBlocking().exchange(request, CategoryListDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnSingleCategory() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/categories/" + testCategory.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleCategoryDto> response = client.toBlocking().exchange(request, SingleCategoryDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals("Example Category", response.getBody().get().getName());
    }

    @Test
    public void shouldFailFetchingOtherUserCategory() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/categories/" + additionalCategory.getId())
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, ErrorResponse.class)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    public void shouldReturnNotFound() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/categories/123456789").headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, SingleCategoryDto.class);
        });
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
        MutableHttpRequest<SingleCategoryDto> request = HttpRequest.POST("/categories", dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleCategoryDto> response = client.toBlocking().exchange(request, SingleCategoryDto.class);
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
        MutableHttpRequest<SingleCategoryDto> request = HttpRequest.POST("/categories", dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleCategoryDto> response = client.toBlocking().exchange(request, SingleCategoryDto.class);
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
        MutableHttpRequest<SingleCategoryDto> request = HttpRequest.PUT("/categories/" + testCategory.getId(), dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleCategoryDto> response = client.toBlocking().exchange(request, SingleCategoryDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        SingleCategoryDto responseDto = response.getBody().get();
        assertEquals(dto.getName(), responseDto.getName());
        assertEquals(dto.getUserId(), responseDto.getUserId());
        assertEquals(BigDecimal.ZERO, responseDto.getBudgeted());
    }

    @Test
    public void shouldFailUpdatingOtherUserCategory() {
        SingleAccountDto dto = new SingleAccountDto();
        dto.setName("New Category");
        dto.setUserId(testUser.getId());
        MutableHttpRequest<SingleAccountDto> req = HttpRequest.PUT("/categories/" + additionalCategory.getId(), dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(req, ErrorResponse.class)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    public void shouldDeleteCategory() {
        MutableHttpRequest<Object> request = HttpRequest.DELETE("/categories/" + testCategory.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleCategoryDto> response = client.toBlocking().exchange(request, SingleCategoryDto.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        List<Category> categoryList = categoryRepository.findByUserId(testUser.getId());
        assertEquals(0, categoryList.size());
    }

    @Test
    public void shouldFailDeletingOtherUserCategory() {
        MutableHttpRequest<Object> req = HttpRequest.DELETE("/categories/" + additionalCategory.getId())
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(req, ErrorResponse.class)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    public void shouldFailWhenNoNameGiven() {
        SingleCategoryDto dto = new SingleCategoryDto();
        dto.setName("");
        dto.setUserId(testUser.getId());
        MutableHttpRequest<SingleCategoryDto> request = HttpRequest.POST("/categories", dto).headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, SingleCategoryDto.class);
        });
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_CATEGORY_NAME", errorResponse.getCode());
    }

    @Test
    public void shouldFailCategoryNesting() {
        SingleCategoryDto dto = new SingleCategoryDto();
        dto.setName("Category");
        dto.setUserId(testUser.getId());
        dto.setParentId(additionalCategory.getId());
        MutableHttpRequest<SingleCategoryDto> request = HttpRequest.POST("/categories", dto).headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, SingleCategoryDto.class);
        });
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_CATEGORY", errorResponse.getCode());
    }

}
