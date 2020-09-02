window.$docsify = {
  name: '',
  nameLink: 'https://gitpitch.com',
  repo: 'gitpitch/gitpitch',
  coverpage: false,
  auto2top: true,
  maxLevel: 4,
  subMaxLevel: 3,
  loadNavbar: true,
  loadSidebar: '_sidebar.md',
  themeColor: '#E67E22',
  externalLinkTarget: '_blank',
  externalLinkRel: 'noopener',
  routerMode: 'hash',
  notFoundPage: 'not-found.md',
  search: {
    maxAge: 86400000,
    paths: 'auto',
    placeholder: 'Type here to search...',
    noData: 'No results.',
    depth: 3,
    hideOtherSidebarContent: true,
    namespace: 'gitpitch-docs-primary'
  },
  tabs: {
    persist    : true,
    sync       : true,
    theme      : 'classic',
    tabComments: true,
    tabHeadings: true
  },
  pagination: {
    previousText: 'PREVIOUS',
    nextText: 'NEXT',
    crossChapter: true,
    crossChapterText: false
  },
  ga: 'UA-XXXXX-Y'
}
