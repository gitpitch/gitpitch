# Code Filters

Code filters are a feature of [code widgets](/code/widgets.md). Code filters let you select and inject specific code from any source file or GitHub GIST onto your slides.

<iframe id="youtube" width="900" height="600" src="https://www.youtube.com/embed/X8stj8p4JM4" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

### Basics

There are two distinct types of code filter:

1. The [lines filter](#lines-filter) is used to select individual lines of code or a range of code from source.
2. The [tags filter](#tags-filter) is used to select lines that fall between *user-defined* tags within source.

?> Lines filters are recommended for source files used exclusively by your slide deck. If your source files are likely to change over time consider using tags filters instead. Tags filters are not dependent on the position of code within a file so will seamlessly sync as code files change.

### Lines Filter

The following snippets demonstrate the use of the *lines* filter to extract one or more code ranges from source code files and GitHub GIST:

<!-- tabs:start -->

#### ** Source Code Widget **

```markdown
@code[javascript, drag=99, fit=1.7](src/demo.js?lines=1-3,5,7-9]
```

```markdown
@code[ruby, drag=60, drop=right, fit=1.22](src/demo.rb?lines=4,6-10]
```

```markdown
@code[golang code-line-numbers, drag=100](src/demo.go?lines=40-44]
```

#### ** GitHub GIST Widget **

```markdown
@gist[elixir, drag=60](josevalim/45898f5468dfec05de04?lines=1,34-38)
```

```markdown
@gist[scala, drag=80](jroper/f28ecf79f4a4be70e3f499a672d8d6b5#filename=SourceWithBackoffSupervision.scala&lines=1-2,5-8)
```

<!-- tabs:end -->

As demonstrated above, the lines filter can extract individual lines of code, ranges of code, or combinations of individual lines and ranges from within any source file or GitHub GIST. The lines of code extracted by a filter are then automatically rendered on your slide.

### Tags Filter

The following snippets demonstrate the use of the *tags* filter to extract one or more code ranges from source code files and GitHub GIST:

<!-- tabs:start -->

#### ** Source Code Widget **

```elixir
# Sample snippet of source code file content.
.
.
def start_link(pipeline) do
  GenServer.start_link(__MODULE__, pipeline)
end

# tag::initpipe[]
def init(pipeline) do
  with {:ok, _} <- validate_modules(pipeline),
       {:ok, _} <- validate_behaviours(pipeline),
       {:ok, _} <- activate_tracing(pipeline),
       state    <- initialize_monitor(pipeline),
    do: start_monitor(state)
end
# end::initpipe[]
.
.
```

```markdown
# Sample code widget w/ tags filter to extract above "initpipe" content.

@code[elixir, drag=99](src/demo.ex?tags=initpipe)
```

#### ** GitHub GIST Widget **

```ruby
# Sample snippet of GitHub GIST content.
.
.
# tag::timings[]
if timings
  timings.record :read
  timings.start :parse
end
# end::timings[]

# tag::parse[]
doc = (options[:parse] == false ?
  (Document.new lines, options) :
  (Document.new lines,options).parse)
# end::parse[]
.
.
```

```markdown
# Sample gist widget w/ tags filter to extract above "timings" and "parse" content.

@gist[ruby, drag=99](onetapbeyond/b3bb5ed063b7683ed51592edf4f80056?tags=timings,parse)

```

<!-- tabs:end -->

Tags are defined using opening **tag** and closing **end** directives as described here:

- To indicate the start of a tagged region, insert a comment line in your code.
- Assign a unique name to the **tag** directive, for example, *initpipe*.
- Insert another comment line where you want the tagged region to end.
- Assign the name of the region you want to terminate to the **end** directive.
- Opening and closing tag directives must be terminated with `[]` syntax.

Note, multiple values can be specified on a `tags` filter to extract multiple code regions from a source code file or GitHub GIST. In these cases, use comma-separated values.

?> Lines filters are recommended for source files used exclusively by your slide deck. If your source files are likely to change over time consider using tags filters instead. Tags filters are not dependent on the position of code within a file so will seamlessly sync as code changes.

