package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Test
    void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
        final String username = "not_found";
        final UUID id = UUID.randomUUID();

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.empty());

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "",
                username,
                true
        );

        CategoryNotFoundException ex = Assertions.assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t find category by id: '" + id + "'",
                ex.getMessage()
        );
    }

    @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
    @ParameterizedTest
    void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                catName,
                username,
                true
        );

        InvalidCategoryNameException ex = Assertions.assertThrows(
                InvalidCategoryNameException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t add category with name: '" + catName + "'",
                ex.getMessage()
        );
    }

    @Test
    void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(false);

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));
        Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "Бары",
                username,
                true
        );

        categoryService.update(categoryJson);
        ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
        verify(categoryRepository).save(argumentCaptor.capture());
        assertEquals("Бары", argumentCaptor.getValue().getName());
        assertEquals("duck", argumentCaptor.getValue().getUsername());
        assertTrue(argumentCaptor.getValue().isArchived());
        assertEquals(id, argumentCaptor.getValue().getId());
    }

    @Test
    void getAllCategoriesShouldFilterArchivedCategories(@Mock CategoryRepository categoryRepository) {
        final String username = "testUser";

        CategoryEntity category1 = new CategoryEntity();
        category1.setId(UUID.randomUUID());
        category1.setName("Category1");
        category1.setUsername(username);
        category1.setArchived(false);

        CategoryEntity category2 = new CategoryEntity();
        category2.setId(UUID.randomUUID());
        category2.setName("Category2");
        category2.setUsername(username);
        category2.setArchived(true);

        List<CategoryEntity> categories = List.of(category1, category2);

        Mockito.when(categoryRepository.findAllByUsernameOrderByName(eq(username))).thenReturn(categories);

        CategoryService categoryService = new CategoryService(categoryRepository);

        List<CategoryJson> result = categoryService.getAllCategories(username, true);

        assertEquals(1, result.size());
        assertEquals("Category1", result.getFirst().name());
        verify(categoryRepository, times(1)).findAllByUsernameOrderByName(eq(username));
    }

    @Test
    void updateShouldThrowTooManyCategoriesException(@Mock CategoryRepository categoryRepository) {
        final String username = "testUser";
        final UUID categoryId = UUID.randomUUID();

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("Existing");
        existingCategory.setUsername(username);
        existingCategory.setArchived(true);

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(categoryId))).thenReturn(Optional.of(existingCategory));
        Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(8L);

        CategoryService categoryService = new CategoryService(categoryRepository);
        CategoryJson categoryToUpdate = new CategoryJson(categoryId, "NewCategory", username, false);

        TooManyCategoriesException ex = assertThrows(
                TooManyCategoriesException.class,
                () -> categoryService.update(categoryToUpdate)
        );

        assertEquals("Can`t unarchive category for user: '" + username + "'", ex.getMessage());
    }

    @Test
    void updateShouldSaveUpdatedCategory(@Mock CategoryRepository categoryRepository) {
        final String username = "testUser";
        final UUID categoryId = UUID.randomUUID();

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("Existing");
        existingCategory.setUsername(username);
        existingCategory.setArchived(true);

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(categoryId))).thenReturn(Optional.of(existingCategory));
        Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(3L);
        Mockito.when(categoryRepository.save(any(CategoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);
        CategoryJson categoryToUpdate = new CategoryJson(categoryId, "UpdatedCategory", username, false);

        CategoryJson result = categoryService.update(categoryToUpdate);

        assertEquals("UpdatedCategory", result.name());
        assertFalse(result.archived());
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    void saveShouldThrowTooManyCategoriesException(@Mock CategoryRepository categoryRepository) {
        final String username = "testUser";

        Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(8L);

        CategoryService categoryService = new CategoryService(categoryRepository);
        CategoryJson newCategory = new CategoryJson(null, "NewCategory", username, false);

        TooManyCategoriesException ex = assertThrows(
                TooManyCategoriesException.class,
                () -> categoryService.save(newCategory)
        );

        assertEquals("Can`t add over than 8 categories for user: '" + username + "'", ex.getMessage());
    }

    @Test
    void saveShouldAddNewCategory(@Mock CategoryRepository categoryRepository) {
        final String username = "testUser";
        final UUID categoryId = UUID.randomUUID();

        CategoryEntity savedCategory = new CategoryEntity();
        savedCategory.setId(categoryId);
        savedCategory.setName("NewCategory");
        savedCategory.setUsername(username);
        savedCategory.setArchived(false);

        Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(3L);
        Mockito.when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(savedCategory);

        CategoryService categoryService = new CategoryService(categoryRepository);
        CategoryJson newCategory = new CategoryJson(null, "NewCategory", username, false);

        CategoryJson result = CategoryJson.fromEntity(categoryService.save(newCategory));

        assertEquals("NewCategory", result.name());
        assertEquals(username, result.username());
        assertFalse(result.archived());
        verify(categoryRepository).save(any(CategoryEntity.class));
    }
}