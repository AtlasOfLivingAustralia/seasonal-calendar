package au.org.ala.jooq.postgres;

import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Int4RangeBinding implements Binding<Object, Range<Integer>> {

    enum Int4RangeConverter implements Converter<Object, Range<Integer>> {

        INSTANCE;

        private static final Pattern PATTERN = Pattern.compile("([\\[(])(.*?),(.*?)([)\\[])");

        @Override
        public Range<Integer> from(Object databaseObject) {

            if (databaseObject == null || "empty".equals(databaseObject)) {
                return Range.empty();
            }
            Matcher m = PATTERN.matcher("" + databaseObject);
            if (m.find()) {
                boolean startInclusive = Objects.equals(m.group(1), "[");
                String start = m.group(2);
                String end = m.group(3);
                boolean endInclusive = Objects.equals(m.group(4), "]");

                return new Range<>(Integer.valueOf(start), startInclusive, Integer.valueOf(end), endInclusive);

            }
            throw new IllegalStateException("Could not parse range from: " + databaseObject);
        }

        @Override
        public Object to(Range<Integer> userObject) {
            return Objects.toString(userObject, "empty");
        }

        @Override
        public Class<Object> fromType() {
            return Object.class;
        }

        @SuppressWarnings({ "unchecked" })
        @Override
        public Class<Range<Integer>> toType() {
            return (Class) Range.class;
        }
    }

    @Override
    public Converter<Object, Range<Integer>> converter() {
        return Int4RangeConverter.INSTANCE;
    }

    @Override
    public void sql(BindingSQLContext<Range<Integer>> ctx) throws SQLException {
        ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::int4range");
    }

    @Override
    public void register(BindingRegisterContext<Range<Integer>> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    public void set(BindingSetStatementContext<Range<Integer>> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), "empty"));
    }

    @Override
    public void set(BindingSetSQLOutputContext<Range<Integer>> ctx) throws SQLException {
        ctx.output().writeString(Objects.toString(ctx.convert(converter()).value(), "empty"));
//        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetResultSetContext<Range<Integer>> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
    }

    @Override
    public void get(BindingGetStatementContext<Range<Integer>> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
    }

    @Override
    public void get(BindingGetSQLInputContext<Range<Integer>> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.input().readString());
//        throw new SQLFeatureNotSupportedException();
    }
}
