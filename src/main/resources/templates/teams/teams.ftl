<#-- @ftlvariable name="teams" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.team.Team>" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="Teams">
    <table>
        <thead>
            <tr>
                <th>Number</th>
                <th>Name</th>
                <th>Region</th>
            </tr>
        </thead>
        <tbody>
        <#list teams as team>
        <tr>
            <td><a href="/teams/${team.number?c}">${team.number?c}</a></td>
            <td>${team.name}</td>
            <td>${layout.region_to_text(team.region)}</td>
        </tr>
        </#list>
        </tbody>
    </table>
</@layout.header>
