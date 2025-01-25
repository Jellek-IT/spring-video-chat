package pl.bronikowski.springchat.backendmain.shared;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OffsetPageRequest implements Pageable {
    private final long offset;
    private final int pageNumber;
    private final int pageSize;
    private final Sort sort;

    public static OffsetPageRequest of(Pageable pageable, long offset) {
        return new OffsetPageRequest(offset, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }

    @Override
    @NonNull
    public Pageable next() {
        return new OffsetPageRequest(this.offset, this.getPageNumber() + 1, this.getPageSize(), this.getSort());
    }

    public OffsetPageRequest previous() {
        return this.getPageNumber() == 0
                ? this
                : new OffsetPageRequest(offset, this.getPageNumber() - 1, this.getPageSize(), this.getSort());
    }

    @Override
    @NonNull
    public Pageable previousOrFirst() {
        return this.hasPrevious() ? this.previous() : this.first();
    }

    @Override
    @NonNull
    public Pageable first() {
        return new OffsetPageRequest(this.offset, 0, this.getPageSize(), this.getSort());
    }

    @Override
    @NonNull
    public Pageable withPage(int pageNumber) {
        return new OffsetPageRequest(this.offset, pageNumber, this.getPageSize(), this.getSort());
    }

    @Override
    public boolean hasPrevious() {
        return this.pageNumber > 0;
    }

    @Override
    public long getOffset() {
        return offset + (long) pageNumber * pageSize;
    }
}
