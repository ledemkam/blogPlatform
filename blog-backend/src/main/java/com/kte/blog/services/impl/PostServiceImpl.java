package com.kte.blog.services.impl;

import com.kte.blog.domain.entities.Category;
import com.kte.blog.domain.entities.Post;
import com.kte.blog.domain.entities.Tag;
import com.kte.blog.domain.entities.User;
import com.kte.blog.domain.enumerations.PostStatus;
import com.kte.blog.domain.request.CreatePostRequest;
import com.kte.blog.repositories.PostRepository;
import com.kte.blog.services.CategoryService;
import com.kte.blog.services.PostService;
import com.kte.blog.services.TagService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    private static final int WORDS_PER_MINUTE = 200;

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
       if(categoryId != null && tagId != null) {
           Category category = categoryService.getCategoryById(categoryId);
           Tag  tag = tagService.getTagById(tagId);
           return postRepository.findAllByStatusAndCategoryAndTagsContaining(
                   PostStatus.PUBLISHED,
                   category,
                   tag
           );
       }

       if(categoryId !=null){
           Category category = categoryService.getCategoryById(categoryId);
           return postRepository.findAllByStatusAndCategory(
                   PostStatus.PUBLISHED,
                   category
           );
       }

         if(tagId != null){
              Tag tag = tagService.getTagById(tagId);
              return postRepository.findAllByStatusAndTagsContaining(
                     PostStatus.PUBLISHED,
                     tag
              );
         }

            return postRepository.findAllByStatus(PostStatus.PUBLISHED);
    }

    @Override
    public List<Post> getDraftPosts(User user) {
        return postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT);
    }

    @Override
    @Transactional
    public Post createPost(User user, CreatePostRequest createPostRequest) {
        Post newPost = new Post();
        newPost.setTitle(createPostRequest.getTitle());
        newPost.setContent(createPostRequest.getContent());
        newPost.setStatus(createPostRequest.getStatus());
        newPost.setAuthor(user);
        newPost.setReadingTime(calculateReadingTime(createPostRequest.getContent()));

        Category category = categoryService.getCategoryById(createPostRequest.getCategoryId());
        newPost.setCategory(category);

        List<Tag> tags = tagService.getTagsByIds(createPostRequest.getTagIds());
        newPost.setTags(new HashSet<>(tags));

        return postRepository.save(newPost);
    }

    private Integer calculateReadingTime(String content) {
        if(content == null || content.isEmpty()) {
            return 0;
        }
        int wortdContent = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wortdContent / WORDS_PER_MINUTE);

    }
}
