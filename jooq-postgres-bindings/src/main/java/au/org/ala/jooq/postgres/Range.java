package au.org.ala.jooq.postgres;

public class Range<T extends Comparable<? super T>> {

    private final T start;
    private final boolean startInclusive;
    private final T end;
    private final boolean endInclusive;

    public Range(T start, T end) {
        this(start, true, end, false);
    }

    public Range(T start, boolean startInclusive, T end, boolean endInclusive) {
        assert end.compareTo(start) >= 0;
        this.start = start;
        this.startInclusive = startInclusive;
        this.end = end;
        this.endInclusive = endInclusive;
    }

    static <U extends Comparable<? super U>> Range<U> empty() {
        return new Range.Empty();
    }

    static final class Empty extends Range {

        private Empty() {
            super(null, null);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public String toString() {
            return "empty";
        }
    }

    public T getStart() {
        return start;
    }

    public boolean isStartInclusive() {
        return startInclusive;
    }

    public T getEnd() {
        return end;
    }

    public boolean isEndInclusive() {
        return endInclusive;
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString() {

        return String.valueOf(startInclusive ? '[' : '(') +
                start +
                ", " +
                end +
                (endInclusive ? ']' : ')');
    }
}
