<#-- @ftlvariable name="teams" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.team.Team>" -->
<#import "/common/layout.ftl" as layout />
<#import "/common/links.ftl" as links />
<#import "/common/enums.ftl" as enums />
<@layout.header title="Teams">
    <section>
        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Region</th>
                </tr>
            </thead>
            <tbody>
            <#list teams?sort_by("number") as team>
                <tr>
                    <td><@links.team_number_link team_number=team.number /></td>
                    <td>${team.name}</td>
                    <td>${enums.region_to_text(team.region)}</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </section>
</@layout.header>
