package dev.t3hw.fstest.db.repository;

import static dev.t3hw.fstest.db.jooq.Tables.POSTS;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.t3hw.fstest.db.jooq.tables.daos.PostsDao;
import dev.t3hw.fstest.db.jooq.tables.pojos.Posts;
import dev.t3hw.fstest.db.jooq.tables.records.PostsRecord;

@Repository
public class PostsRepository extends PostsDao {

    private final DSLContext ctx;

    public PostsRepository(DSLContext ctx, Configuration configuration) {
        super(configuration);
        this.ctx = ctx;
    }

    @Transactional
    public Posts create(Posts post) {
        PostsRecord postRecord = ctx.newRecord(POSTS, post);
        postRecord.store();
        return postRecord.into(Posts.class);
    }

    @Transactional
    public boolean existsByIdForUpdate(Long id) {
        return ctx.fetchExists(ctx.selectOne()
                .from(POSTS)
                .where(POSTS.ID.eq(id))
                .forUpdate());
    }

    @Transactional
    public Posts updateContent(Posts post) {
        ctx.update(POSTS)
                .set(POSTS.CONTENT, post.getContent())
                .where(POSTS.ID.eq(post.getId()))
                .execute();
        return findById(post.getId());
    }
}