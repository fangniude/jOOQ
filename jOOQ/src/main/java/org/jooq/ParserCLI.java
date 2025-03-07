/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.conf.ParseNameCase;
import org.jooq.conf.ParseUnknownFunctions;
import org.jooq.conf.ParseUnsupportedSyntax;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderOptionalKeyword;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.TransformUnneededArithmeticExpressions;
import org.jooq.conf.Transformation;
import org.jooq.impl.DSL;
import org.jooq.impl.ParserException;

/**
 * A command line interface to the Parser API, which works in a similar way as
 * <a href="https://www.jooq.org/translate">https://www.jooq.org/translate</a>.
 *
 * @author Lukas Eder
 */
public final class ParserCLI {

    private static final Pattern FLAG = Pattern.compile("^/([\\w\\-]+)(?:\\s+(\\w+))?\\s*$");

    public static final void main(String... args) throws Exception {
        CLIUtil.main("https://www.jooq.org/doc/latest/manual/sql-building/sql-parser/sql-parser-cli/", () -> {
            Args a;
            Settings settings = new Settings();
            DSLContext ctx;

            a = parse(args);
            settings(a, settings);
            ctx = ctx(a, settings);

            if (a.interactive || args == null || args.length == 0) {
                interactiveMode(ctx, a);
            }
            else if (a.done) {}
            else if (a.toDialect == null || a.sql == null) {
                System.out.println("Mandatory arguments: -T and -s. Use -h for help");
                throw new RuntimeException();
            }
            else
                render(ctx, a);
        });
    }

    private static final DSLContext ctx(Args a, Settings settings) {
        return DSL.using(a.toDialect, settings);
    }

    private static final void settings(Args a, Settings settings) {
        if (a.formatted != null)
            settings.setRenderFormatted(a.formatted);
        if (a.keywords != null)
            settings.setRenderKeywordCase(a.keywords);
        if (a.name != null)
            settings.setRenderNameCase(a.name);
        if (a.quoted != null)
            settings.setRenderQuotedNames(a.quoted);
        if (a.fromDialect != null)
            settings.setParseDialect(a.fromDialect);
        if (a.parseDateFormat != null)
            settings.setParseDateFormat(a.parseDateFormat);
        if (a.parseIgnoreComments != null)
            settings.setParseIgnoreComments(a.parseIgnoreComments);
        if (a.parseIgnoreCommentStart != null)
            settings.setParseIgnoreCommentStart(a.parseIgnoreCommentStart);
        if (a.parseIgnoreCommentStop != null)
            settings.setParseIgnoreCommentStop(a.parseIgnoreCommentStop);
        if (a.parseLocale != null)
            settings.setParseLocale(a.parseLocale);
        if (a.parseNameCase != null)
            settings.setParseNameCase(a.parseNameCase);
        if (a.parseNamedParamPrefix != null)
            settings.setParseNamedParamPrefix(a.parseNamedParamPrefix);
        if (a.parseRetainCommentsBetweenQueries != null)
            settings.setParseRetainCommentsBetweenQueries(a.parseRetainCommentsBetweenQueries);
        if (a.parseSetCommands != null)
            settings.setParseSetCommands(a.parseSetCommands);
        if (a.parseTimestampFormat != null)
            settings.setParseTimestampFormat(a.parseTimestampFormat);
        if (a.parseUnknownFunctions != null)
            settings.setParseUnknownFunctions(a.parseUnknownFunctions);
        if (a.renderCoalesceToEmptyStringInConcat != null)
            settings.setRenderCoalesceToEmptyStringInConcat(a.renderCoalesceToEmptyStringInConcat);
        if (a.renderOptionalInnerKeyword != null)
            settings.setRenderOptionalInnerKeyword(a.renderOptionalInnerKeyword);
        if (a.renderOptionalOuterKeyword != null)
            settings.setRenderOptionalOuterKeyword(a.renderOptionalOuterKeyword);
        if (a.renderOptionalAsKeywordForFieldAliases != null)
            settings.setRenderOptionalAsKeywordForFieldAliases(a.renderOptionalAsKeywordForFieldAliases);
        if (a.renderOptionalAsKeywordForTableAliases != null)
            settings.setRenderOptionalAsKeywordForTableAliases(a.renderOptionalAsKeywordForTableAliases);
        if (a.transformAnsiJoinToTableLists != null)
            settings.setTransformAnsiJoinToTableLists(a.transformAnsiJoinToTableLists);
        if (a.transformTableListsToAnsiJoin != null)
            settings.setTransformTableListsToAnsiJoin(a.transformTableListsToAnsiJoin);
        if (a.transformUnneededArithmetic != null)
            settings.setTransformUnneededArithmeticExpressions(a.transformUnneededArithmetic);
        if (a.transformQualify != null)
            settings.setTransformRownum(a.transformQualify);
        if (a.transformRownum != null)
            settings.setTransformRownum(a.transformRownum);
    }

    private static final <E extends Enum<E>> void parseInteractive(
        Class<E> type,
        String arg,
        Consumer<? super E> onSuccess,
        Runnable display
    ) {
        try {
            if (arg != null)
                onSuccess.accept(Enum.valueOf(type, arg.toUpperCase()));

            display.run();
        }
        catch (IllegalArgumentException e) {
            invalid(arg, type);
        }
    }

    private static final void interactiveMode(DSLContext ctx, Args a) {
        Scanner scan = new Scanner(System.in);

        System.out.print("> ");

        cliLoop:
        do {
            String line = scan.nextLine();

            // TODO: Allow reading history again using arrow keys
            // https://stackoverflow.com/q/572001/521799
            a.history.add(line);

            // [#12543] Avoid interpreting comments as commands
            boolean leadingSlash = line.matches("^/[^/*].*$");

            if (a.sql == null && leadingSlash) {
                if ("/q".equals(line) || "/quit".equals(line) ||
                    "/e".equals(line) || "/exit".equals(line)) {
                    System.out.println("Bye");
                    break cliLoop;
                }
                else if ("/?".equals(line) || "/h".equals(line) || "/help".equals(line))
                    helpInteractive();
                else if ("/d".equals(line) || "/display".equals(line))
                    displayArguments(a);
                else {
                    Matcher matcher = FLAG.matcher(line);

                    if (matcher.find()) {
                        String flag = matcher.group(1);
                        String arg = matcher.group(2);

                        if (flag != null) {
                            if ("f".equals(flag) || "formatted".equals(flag)) {
                                if (arg != null)
                                    a.formatted = Boolean.parseBoolean(arg.toLowerCase());

                                displayFormatted(a);
                            }
                            else if ("k".equals(flag) || "keyword".equals(flag))
                                parseInteractive(RenderKeywordCase.class, arg, e -> { a.keywords = e; }, () -> displayKeywords(a));
                            else if ("i".equals(flag) || "identifier".equals(flag))
                                parseInteractive(RenderNameCase.class, arg, e -> { a.name = e; }, () -> displayIdentifiers(a));
                            else if ("Q".equals(flag) || "quoted".equals(flag))
                                parseInteractive(RenderQuotedNames.class, arg, e -> { a.quoted = e; }, () -> displayQuoted(a));
                            else if ("F".equals(flag) || "from-dialect".equals(flag))
                                parseInteractive(SQLDialect.class, arg, e -> { a.fromDialect = e; }, () -> displayFromDialect(a));
                            else if ("render-coalesce-to-empty-string-in-concat".equals(flag)) {
                                if (arg != null)
                                    a.renderCoalesceToEmptyStringInConcat = Boolean.parseBoolean(arg.toLowerCase());

                                displayRenderCoalesceToEmptyStringInConcat(a);
                            }
                            else if ("parse-date-format".equals(flag)) {
                                if (arg != null)
                                    a.parseDateFormat = arg;

                                displayParseDateFormat(a);
                            }
                            else if ("parse-ignore-comments".equals(flag)) {
                                if (arg != null)
                                    a.parseIgnoreComments = Boolean.parseBoolean(arg.toLowerCase());

                                displayParseIgnoreComments(a);
                            }
                            else if ("parse-ignore-comment-start".equals(flag)) {
                                if (arg != null)
                                    a.parseIgnoreCommentStart = arg;

                                displayParseIgnoreCommentStart(a);
                            }
                            else if ("parse-ignore-comment-stop".equals(flag)) {
                                if (arg != null)
                                    a.parseIgnoreCommentStop = arg;

                                displayParseIgnoreCommentStop(a);
                            }
                            else if ("parse-locale".equals(flag)) {
                                if (arg != null)
                                    a.parseLocale = Locale.forLanguageTag(arg);

                                displayParseLocale(a);
                            }
                            else if ("parse-name-case".equals(flag))
                                parseInteractive(ParseNameCase.class, arg, e -> { a.parseNameCase = e; }, () -> displayParseNameCase(a));
                            else if ("parse-named-param-prefix".equals(flag)) {
                                if (arg != null)
                                    a.parseNamedParamPrefix = arg;

                                displayParseNamedParamPrefix(a);
                            }
                            else if ("parse-retain-comments-between-queries".equals(flag)) {
                                if (arg != null)
                                    a.parseRetainCommentsBetweenQueries = Boolean.parseBoolean(arg.toLowerCase());

                                displayParseRetainCommentsBetweenQueries(a);
                            }
                            else if ("parse-set-commands".equals(flag)) {
                                if (arg != null)
                                    a.parseSetCommands = Boolean.parseBoolean(arg.toLowerCase());

                                displayParseSetCommands(a);
                            }
                            else if ("parse-timestamp-format".equals(flag)) {
                                if (arg != null)
                                    a.parseTimestampFormat = arg;

                                displayParseTimestampFormat(a);
                            }
                            else if ("parse-unknown-functions".equals(flag))
                                parseInteractive(ParseUnknownFunctions.class, arg, e -> { a.parseUnknownFunctions = e; }, () -> displayParseUnknownFunctions(a));
                            else if ("parse-unsupported-syntax".equals(flag))
                                parseInteractive(ParseUnsupportedSyntax.class, arg, e -> { a.parseUnsupportedSyntax = e; }, () -> displayParseUnsupportedSyntax(a));
                            else if ("render-optional-inner-keyword".equals(flag))
                                parseInteractive(RenderOptionalKeyword.class, arg, e -> { a.renderOptionalInnerKeyword = e; }, () -> displayRenderOptionalInnerKeyword(a));
                            else if ("render-optional-outer-keyword".equals(flag))
                                parseInteractive(RenderOptionalKeyword.class, arg, e -> { a.renderOptionalOuterKeyword = e; }, () -> displayRenderOptionalOuterKeyword(a));
                            else if ("render-optional-as-keyword-for-field-aliases".equals(flag))
                                parseInteractive(RenderOptionalKeyword.class, arg, e -> { a.renderOptionalAsKeywordForFieldAliases = e; }, () -> displayRenderOptionalAsKeywordForFieldAliases(a));
                            else if ("render-optional-as-keyword-for-table-aliases".equals(flag))
                                parseInteractive(RenderOptionalKeyword.class, arg, e -> { a.renderOptionalAsKeywordForTableAliases = e; }, () -> displayRenderOptionalAsKeywordForTableAliases(a));
                            else if ("transform-ansi-join-to-table-lists".equals(flag)) {
                                if (arg != null)
                                    a.transformAnsiJoinToTableLists = Boolean.parseBoolean(arg.toLowerCase());

                                displayTransformAnsiJoinToTablesLists(a);
                            }
                            else if ("transform-qualify".equals(flag))
                                parseInteractive(Transformation.class, arg, e -> { a.transformQualify = e; }, () -> displayTransformQualify(a));
                            else if ("transform-rownum".equals(flag))
                                parseInteractive(Transformation.class, arg, e -> { a.transformRownum = e; }, () -> displayTransformRownum(a));
                            else if ("transform-table-lists-to-ansi-join".equals(flag)) {
                                if (arg != null)
                                    a.transformTableListsToAnsiJoin = Boolean.parseBoolean(arg.toLowerCase());

                                displayTransformTableListsToAnsiJoin(a);
                            }
                            else if ("transform-unneeded-arithmetic".equals(flag))
                                parseInteractive(TransformUnneededArithmeticExpressions.class, arg, e -> { a.transformUnneededArithmetic = e; }, () -> { displayTransformUnneededArithmetic(a); });

                            // [#9144] /t maintained for backwards compatibility
                            else if ("t".equals(flag) || "T".equals(flag) || "to-dialect".equals(flag))
                                parseInteractive(SQLDialect.class, arg, e -> { a.toDialect = e; }, () -> displayToDialect(a));
                        }
                    }
                    else {
                        System.out.println("Unrecognised command: " + line);
                        System.out.println("Type /h for help");
                    }
                }

                settings(a, ctx.settings());
                ctx = ctx(a, ctx.settings());
            }

            if (a.sql != null || !leadingSlash) {
                if (a.sql == null)
                    a.sql = line;
                else
                    a.sql = a.sql + "\n" + line;

                if (a.sql.trim().endsWith(";")) {
                    render(ctx, a);
                    a.sql = null;
                    System.out.println();
                }
            }

            System.out.print("> ");
        }
        while (scan.hasNextLine());
    }

    private static final void displayArguments(Args a) {
        displayFormatted(a);
        displayFromDialect(a);
        displayToDialect(a);
        displayKeywords(a);
        displayIdentifiers(a);
        displayQuoted(a);
        displayTransformAnsiJoinToTablesLists(a);
        displayTransformQualify(a);
        displayTransformRownum(a);
        displayTransformTableListsToAnsiJoin(a);
        displayTransformUnneededArithmetic(a);
    }

    private static void displayIdentifiers(Args a) {
        System.out.println("Identifiers                        : " + a.name);
    }

    private static void displayQuoted(Args a) {
        System.out.println("Quoted                             : " + a.quoted);
    }

    private static void displayKeywords(Args a) {
        System.out.println("Keywords                           : " + a.keywords);
    }

    private static void displayToDialect(Args a) {
        System.out.println("To dialect                         : " + a.toDialect);
    }

    private static void displayFromDialect(Args a) {
        System.out.println("From dialect                       : " + a.fromDialect);
    }

    private static void displayFormatted(Args a) {
        System.out.println("Formatted                          : " + a.formatted);
    }

    private static void displayParseDateFormat(Args a) {
        System.out.println("Parse date format                  : " + a.parseDateFormat);
    }

    private static void displayParseIgnoreComments(Args a) {
        System.out.println("Parse ignore comments              : " + a.parseIgnoreComments);
    }

    private static void displayParseIgnoreCommentStart(Args a) {
        System.out.println("Parse ignore comment start         : " + a.parseIgnoreCommentStart);
    }

    private static void displayParseIgnoreCommentStop(Args a) {
        System.out.println("Parse ignore comment stop          : " + a.parseIgnoreCommentStop);
    }

    private static void displayParseLocale(Args a) {
        System.out.println("Parse locale                       : " + a.parseLocale);
    }

    private static void displayParseNameCase(Args a) {
        System.out.println("Parse name case                    : " + a.parseNameCase);
    }

    private static void displayParseNamedParamPrefix(Args a) {
        System.out.println("Parse named param prefix           : " + a.parseNamedParamPrefix);
    }

    private static void displayParseRetainCommentsBetweenQueries(Args a) {
        System.out.println("Retain comments between queries    : " + a.parseRetainCommentsBetweenQueries);
    }

    private static void displayParseSetCommands(Args a) {
        System.out.println("Parse set commands                 : " + a.parseSetCommands);
    }

    private static void displayParseTimestampFormat(Args a) {
        System.out.println("Parse timestamp format             : " + a.parseTimestampFormat);
    }

    private static void displayParseUnknownFunctions(Args a) {
        System.out.println("Parse unknown functions            : " + a.parseUnknownFunctions);
    }

    private static void displayParseUnsupportedSyntax(Args a) {
        System.out.println("Parse unsupported syntax           : " + a.parseUnsupportedSyntax);
    }

    private static void displayRenderCoalesceToEmptyStringInConcat(Args a) {
        System.out.println("Render COALESCE(X, '') in CONCAT   : " + a.renderCoalesceToEmptyStringInConcat);
    }

    private static void displayRenderOptionalInnerKeyword(Args a) {
        System.out.println("Render INNER keyword in INNER JOIN : " + a.renderOptionalInnerKeyword);
    }

    private static void displayRenderOptionalOuterKeyword(Args a) {
        System.out.println("Render OUTER keyword in OUTER JOIN : " + a.renderOptionalOuterKeyword);
    }

    private static void displayRenderOptionalAsKeywordForFieldAliases(Args a) {
        System.out.println("Render AS keyword to alias fields  :" + a.renderOptionalAsKeywordForFieldAliases);
    }

    private static void displayRenderOptionalAsKeywordForTableAliases(Args a) {
        System.out.println("Render AS keyword to alias tables  :" + a.renderOptionalAsKeywordForTableAliases);
    }

    private static void displayTransformAnsiJoinToTablesLists(Args a) {
        System.out.println("Transform ANSI join to table lists : " + a.transformAnsiJoinToTableLists);
    }

    private static void displayTransformQualify(Args a) {
        System.out.println("Transform QUALIFY                  : " + a.transformQualify);
    }

    private static void displayTransformRownum(Args a) {
        System.out.println("Transform ROWNUM                   : " + a.transformRownum);
    }

    private static void displayTransformTableListsToAnsiJoin(Args a) {
        System.out.println("Transform table lists to ANSI join : " + a.transformTableListsToAnsiJoin);
    }

    private static void displayTransformUnneededArithmetic(Args a) {
        System.out.println("Transform unneeded arithmetic      : " + a.transformUnneededArithmetic);
    }

    private static final void render(DSLContext ctx, Args a) {
        String sql = a.sql.trim();

        try {







                System.out.println(ctx.render(ctx.parser().parse(a.sql)));
        }
        catch (ParserException e1) {
            ParserException e = e1;

            if (!sql.matches("^(?is:(?:ALTER|BEGIN|COMMENT|CREATE|DECLARE|DELETE|DESCRIBE|DROP|GRANT|INSERT|MERGE|RENAME|REVOKE|SELECT|SET|SHOW|TRUNCATE|UPDATE|USE).*)$")) {
                try {
                    System.out.println(ctx.render(ctx.parser().parseField(a.sql)));
                }
                catch (ParserException e2) {
                    e = e1.position() >= e2.position() ? e1 : e2;
                }
            }

            System.out.println(e.getMessage());
        }
    }

    private static final <E extends Enum<E>> E parse(Class<E> type, String value) {
        try {
            return Enum.valueOf(type, value.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            invalid(value, type);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private static final <E extends Enum<E>> Args parse(String[] args) {
        Args result = new Args();

        for (int i = 0; i < args.length; i++) {
            Class<? extends Enum<?>> enumArgument = null;

            try {
                if ("-f".equals(args[i]) || "--formatted".equals(args[i]))
                    result.formatted = true;
                else if ("-k".equals(args[i]) || "--keyword".equals(args[i]))
                    result.keywords = parse((Class<RenderKeywordCase>) (enumArgument = RenderKeywordCase.class), args[++i]);
                else if ("-i".equals(args[i]) || "--identifier".equals(args[i]))
                    result.name = parse((Class<RenderNameCase>) (enumArgument = RenderNameCase.class), args[++i]);
                else if ("-Q".equals(args[i]) || "--quoted".equals(args[i]))
                    result.quoted = parse((Class<RenderQuotedNames>) (enumArgument = RenderQuotedNames.class), args[++i]);
                else if ("-F".equals(args[i]) || "--from-dialect".equals(args[i]))
                    result.fromDialect = parse((Class<SQLDialect>) (enumArgument = SQLDialect.class), args[++i]);

                // [#9144] -t maintained for backwards compatibility
                else if ("-t".equals(args[i]) || "-T".equals(args[i]) || "--to-dialect".equals(args[i]))
                    result.toDialect = parse((Class<SQLDialect>) (enumArgument = SQLDialect.class), args[++i]);
                else if ("--parse-date-format".equals(args[i]))
                    result.parseDateFormat = args[++i];
                else if ("--parse-ignore-comments".equals(args[i]))
                    result.parseIgnoreComments = true;
                else if ("--parse-ignore-comment-start".equals(args[i]))
                    result.parseIgnoreCommentStart = args[++i];
                else if ("--parse-ignore-comment-stop".equals(args[i]))
                    result.parseIgnoreCommentStop = args[++i];
                else if ("--parse-locale".equals(args[i]))
                    result.parseLocale = Locale.forLanguageTag(args[++i]);
                else if ("--parse-name-case".equals(args[i]))
                    result.parseNameCase = parse((Class<ParseNameCase>) (enumArgument = ParseNameCase.class), args[++i]);
                else if ("--parse-named-param-prefix".equals(args[i]))
                    result.parseNamedParamPrefix = args[++i];
                else if ("--parse-set-commands".equals(args[i]))
                    result.parseSetCommands = true;
                else if ("--parse-retain-comments-between-queries".equals(args[i]))
                    result.parseRetainCommentsBetweenQueries = true;
                else if ("--parse-timestamp-format".equals(args[i]))
                    result.parseTimestampFormat = args[++i];
                else if ("--parse-unknown-functions".equals(args[i]))
                    result.parseUnknownFunctions = parse((Class<ParseUnknownFunctions>) (enumArgument = ParseUnknownFunctions.class), args[++i]);
                else if ("--parse-unsupported-syntax".equals(args[i]))
                    result.parseUnsupportedSyntax = parse((Class<ParseUnsupportedSyntax>) (enumArgument = ParseUnsupportedSyntax.class), args[++i]);
                else if ("--render-coalesce-to-empty-string-in-concat".equals(args[i]))
                    result.renderCoalesceToEmptyStringInConcat = true;
                else if ("--render-optional-inner-keyword".equals(args[i]))
                    result.renderOptionalInnerKeyword = parse((Class<RenderOptionalKeyword>) (enumArgument = RenderOptionalKeyword.class), args[++i]);
                else if ("--render-optional-outer-keyword".equals(args[i]))
                    result.renderOptionalOuterKeyword = parse((Class<RenderOptionalKeyword>) (enumArgument = RenderOptionalKeyword.class), args[++i]);
                else if ("--render-optional-as-keyword-for-field-aliases".equals(args[i]))
                    result.renderOptionalAsKeywordForFieldAliases = parse((Class<RenderOptionalKeyword>) (enumArgument = RenderOptionalKeyword.class), args[++i]);
                else if ("--render-optional-as-keyword-for-table-aliases".equals(args[i]))
                    result.renderOptionalAsKeywordForTableAliases = parse((Class<RenderOptionalKeyword>) (enumArgument = RenderOptionalKeyword.class), args[++i]);
                else if ("--transform-ansi-join-to-table-lists".equals(args[i]))
                    result.transformAnsiJoinToTableLists = true;
                else if ("--transform-qualify".equals(args[i]))
                    result.transformQualify = parse((Class<Transformation>) (enumArgument = Transformation.class), args[++i]);
                else if ("--transform-rownum".equals(args[i]))
                    result.transformRownum = parse((Class<Transformation>) (enumArgument = Transformation.class), args[++i]);
                else if ("--transform-table-lists-to-ansi-join".equals(args[i]))
                    result.transformTableListsToAnsiJoin = true;
                else if ("--transform-unneeded-arithmetic".equals(args[i]))
                    result.transformUnneededArithmetic = parse((Class<TransformUnneededArithmeticExpressions>) (enumArgument = TransformUnneededArithmeticExpressions.class), args[++i]);
                else if ("-s".equals(args[i]) || "--sql".equals(args[i]))
                    result.sql = args[++i];
                else if ("-I".equals(args[i]) || "--interactive".equals(args[i]))
                    result.interactive = true;
                else if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                    help();
                    result.done = true;
                }
                else {
                    System.out.println("Unknown flag: " + args[i] + ". Use -h or --help");
                    throw new RuntimeException();
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Flag " + args[i - 1] + " requires <" + (enumArgument != null ? enumArgument.getName() : "Unknown") + "> argument");
                throw e;
            }
        }

        return result;
    }

    private static final void invalid(String string, Class<? extends Enum<?>> type) {
        System.out.println("Invalid " + type.getSimpleName() + ": " + string);
        System.out.println("Possible values:");

        for (Enum<?> e : type.getEnumConstants())
            System.out.println("  " + e.name());
    }

    private static final void help() {
        System.out.println("Usage:");
        System.out.println("  -f / --formatted                                                    Format output SQL");
        System.out.println("  -h / --help                                                         Display this help");
        System.out.println("  -k / --keyword                                  <RenderKeywordCase> Specify the output keyword case (org.jooq.conf.RenderKeywordCase)");
        System.out.println("  -i / --identifier                               <RenderNameCase>    Specify the output identifier case (org.jooq.conf.RenderNameCase)");
        System.out.println("  -Q / --quoted                                   <RenderQuotedNames> Specify the output identifier quoting (org.jooq.conf.RenderQuotedNames)");
        System.out.println("  -F / --from-dialect                             <SQLDialect>        Specify the input dialect (org.jooq.SQLDialect)");
        System.out.println("  -T / --to-dialect                               <SQLDialect>        Specify the output dialect (org.jooq.SQLDialect)");
        System.out.println("  -s / --sql                                      <String>            Specify the input SQL string");
        System.out.println("");
        System.out.println("Additional flags:");
        System.out.println("  --parse-date-format                             <String>");
        System.out.println("  --parse-locale                                  <Locale>");
        System.out.println("  --parse-name-case                               <ParseNameCase>");
        System.out.println("  --parse-named-param-prefix                      <String>");
        System.out.println("  --parse-retain-comments-between-queries");
        System.out.println("  --parse-set-commands");
        System.out.println("  --parse-timestamp-format                        <String>");
        System.out.println("  --parse-unknown-functions                       <ParseUnknownFunctions>");
        System.out.println("  --parse-unsupported-syntax                      <ParseUnsupportedSyntax>");
        System.out.println("  --render-optional-inner-keyword                 <RenderOptionalKeyword>");
        System.out.println("  --render-optional-outer-keyword                 <RenderOptionalKeyword>");
        System.out.println("  --render-optional-as-keyword-for-field-aliases  <RenderOptionalKeyword>");
        System.out.println("  --render-optional-as-keyword-for-table-aliases  <RenderOptionalKeyword>");
        System.out.println("");
        System.out.println("Commercial distribution only features:");
        System.out.println("  --render-coalesce-to-empty-string-in-concat");
        System.out.println("  --transform-ansi-join-to-table-lists");
        System.out.println("  --transform-qualify                             <Transformation>");
        System.out.println("  --transform-rownum                              <Transformation>");
        System.out.println("  --transform-table-lists-to-ansi-join");
        System.out.println("  --transform-unneeded-arithmetic                 <TransformUnneededArithmeticExpressions>");
        System.out.println("");
        System.out.println("  -I / --interactive                                               Start interactive mode");
    }

    private static final void helpInteractive() {
        System.out.println("Usage:");
        System.out.println("  /d  or  /display                                                   Display arguments");
        System.out.println("  /f  or  /formatted                             <boolean>           Format output SQL");
        System.out.println("  /h  or  /help                                                      Display this help");
        System.out.println("  /k  or  /keyword                               <RenderKeywordCase> Specify the output keyword case (org.jooq.conf.RenderKeywordCase)");
        System.out.println("  /i  or  /identifier                            <RenderNameCase>    Specify the output identifier case (org.jooq.conf.RenderNameCase)");
        System.out.println("  /Q  or  /quoted                                <RenderQuotedNames> Specify the output identifier quoting (org.jooq.conf.RenderQuotedNames)");
        System.out.println("  /F  or  /from-dialect                          <SQLDialect>        Specify the input dialect (org.jooq.SQLDialect)");
        System.out.println("  /T  or  /to-dialect                            <SQLDialect>        Specify the output dialect (org.jooq.SQLDialect)");
        System.out.println("                                                 <String>            Specify the input SQL string");
        System.out.println("");
        System.out.println("Additional flags:");
        System.out.println("  /parse-date-format                             <String>");
        System.out.println("  /parse-locale                                  <Locale>");
        System.out.println("  /parse-name-case                               <ParseNameCase>");
        System.out.println("  /parse-named-param-prefix                      <String>");
        System.out.println("  /parse-retain-comments-between-queries         <boolean>");
        System.out.println("  /parse-set-commands                            <boolean>");
        System.out.println("  /parse-timestamp-format                        <String>");
        System.out.println("  /parse-unknown-functions                       <ParseUnknownFunctions>");
        System.out.println("  /parse-unsupported-syntax                      <ParseUnsupportedSyntax>");
        System.out.println("  /render-optional-inner-keyword                 <RenderOptionalKeyword>");
        System.out.println("  /render-optional-outer-keyword                 <RenderOptionalKeyword>");
        System.out.println("  /render-optional-as-keyword-for-field-aliases  <RenderOptionalKeyword>");
        System.out.println("  /render-optional-as-keyword-for-table-aliases  <RenderOptionalKeyword>");
        System.out.println("");
        System.out.println("Commercial distribution only features:");
        System.out.println("  /render-coalesce-to-empty-string-in-concat     <boolean>");
        System.out.println("  /transform-ansi-join-to-table-lists            <boolean>");
        System.out.println("  /transform-qualify                             <Transformation>");
        System.out.println("  /transform-rownum                              <Transformation>");
        System.out.println("  /transform-table-lists-to-ansi-join            <boolean>");
        System.out.println("  /transform-unneeded-arithmetic                 <TransformUnneededArithmeticExpressions>");
        System.out.println("");
        System.out.println("  /q  or  /quit   Quit");
        System.out.println("  /e  or  /exit   Also quit");
    }

    public static final class Args {
        Settings                               d                                      = new Settings();
        List<String>                           history                                = new ArrayList<>();
        boolean                                interactive;
        boolean                                done;

        String                                 sql;
        RenderKeywordCase                      keywords                               = RenderKeywordCase.LOWER;
        RenderNameCase                         name                                   = RenderNameCase.LOWER;
        RenderQuotedNames                      quoted                                 = RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED;
        SQLDialect                             toDialect                              = SQLDialect.DEFAULT;
        SQLDialect                             fromDialect                            = SQLDialect.DEFAULT;
        Boolean                                formatted;
        Boolean                                renderCoalesceToEmptyStringInConcat;
        RenderOptionalKeyword                  renderOptionalInnerKeyword             = RenderOptionalKeyword.DEFAULT;
        RenderOptionalKeyword                  renderOptionalOuterKeyword             = RenderOptionalKeyword.DEFAULT;
        RenderOptionalKeyword                  renderOptionalAsKeywordForFieldAliases = RenderOptionalKeyword.DEFAULT;
        RenderOptionalKeyword                  renderOptionalAsKeywordForTableAliases = RenderOptionalKeyword.DEFAULT;
        String                                 parseDateFormat                        = d.getParseDateFormat();
        Boolean                                parseIgnoreComments                    = d.isParseIgnoreComments();
        String                                 parseIgnoreCommentStart                = d.getParseIgnoreCommentStart();
        String                                 parseIgnoreCommentStop                 = d.getParseIgnoreCommentStop();
        Locale                                 parseLocale                            = d.getParseLocale();
        ParseNameCase                          parseNameCase                          = d.getParseNameCase();
        String                                 parseNamedParamPrefix                  = d.getParseNamedParamPrefix();
        Boolean                                parseRetainCommentsBetweenQueries      = d.isParseRetainCommentsBetweenQueries();
        Boolean                                parseSetCommands                       = d.isParseSetCommands();
        String                                 parseTimestampFormat                   = d.getParseTimestampFormat();
        ParseUnknownFunctions                  parseUnknownFunctions                  = d.getParseUnknownFunctions();
        ParseUnsupportedSyntax                 parseUnsupportedSyntax                 = d.getParseUnsupportedSyntax();
        Boolean                                transformAnsiJoinToTableLists;
        Transformation                         transformQualify;
        Transformation                         transformRownum;
        Boolean                                transformTableListsToAnsiJoin;
        TransformUnneededArithmeticExpressions transformUnneededArithmetic            = TransformUnneededArithmeticExpressions.NEVER;
    }
}
