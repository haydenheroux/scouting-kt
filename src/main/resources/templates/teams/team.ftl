<#-- @ftlvariable name="team" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#import "/common/layout.ftl" as layout />
<#import "/common/links.ftl" as links />
<#import "/common/sections.ftl" as sections />
<@layout.header title="Team ${team.number?c}">
    <h1><@links.team_link team=team /></h1>
    <#list team.seasons?reverse as season>
        <@sections.season_section team=team season=season />
    </#list>
</@layout.header>
