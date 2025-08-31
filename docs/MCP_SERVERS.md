# MCP Servers Configuration

This project includes Model Context Protocol (MCP) server configurations for enhanced AI assistance in Cursor. MCP servers provide additional capabilities and context for AI-powered development.

## 🚀 What are MCP Servers?

Model Context Protocol (MCP) servers are external services that provide additional context and capabilities to AI assistants. They enable AI to:
- Access external data sources
- Interact with APIs and services
- Provide real-time information
- Enhance development workflows

## 📋 Available MCP Servers

### 1. **GitHub MCP Server**
- **Purpose**: Access GitHub repositories, issues, pull requests, and workflows
- **Capabilities**: 
  - Repository management
  - Issue and PR operations
  - Workflow management
  - Code search and analysis
- **Use Cases**: 
  - Automated PR reviews
  - Repository analysis
  - Workflow automation
  - Code quality checks

### 2. **Context7 MCP Server**
- **Purpose**: Access comprehensive library documentation and code examples
- **Capabilities**:
  - Library documentation search
  - Code snippet retrieval
  - API reference access
  - Best practices and examples
- **Use Cases**:
  - Library integration guidance
  - API usage examples
  - Documentation lookup
  - Code pattern discovery

## ⚙️ Installation & Setup

### **Prerequisites**
- [Cursor](https://cursor.sh/) editor installed
- Node.js (for GitHub MCP server)
- Valid API keys for the services

### **Step 1: Install MCP Servers**

The MCP servers are configured in `.cursor/mcp.json` and will be automatically installed when you open the project in Cursor.

#### **GitHub MCP Server**
```bash
# The server will be automatically installed via npx
# No manual installation required
```

#### **Context7 MCP Server**
```bash
# The server is accessed via HTTPS
# No local installation required
```

### **Step 2: Configure Environment Variables**

Create a `.env` file in your project root or set these environment variables in your system:

```bash
# GitHub API Token
PERSONAL_GITHUB_API_KEY=ghp_your_github_token_here

# Context7 API Key
PERSONAL_CONTEXT7_API_KEY=your_context7_api_key_here
```

#### **GitHub API Token Setup**
1. Go to [GitHub Settings > Developer settings > Personal access tokens](https://github.com/settings/tokens)
2. Click "Generate new token (classic)"
3. Select the following scopes:
   - `repo` - Full control of private repositories
   - `workflow` - Update GitHub Action workflows
   - `write:packages` - Upload packages to GitHub Package Registry
   - `delete:packages` - Delete packages from GitHub Package Registry
4. Copy the generated token
5. Set as `PERSONAL_GITHUB_API_KEY` environment variable

#### **Context7 API Key Setup**
1. Visit [Context7](https://mcp.context7.com/)
2. Sign up for an account
3. Generate an API key
4. Set as `PERSONAL_CONTEXT7_API_KEY` environment variable

### **Step 3: Verify Configuration**

The MCP servers should automatically connect when you open the project in Cursor. You can verify the connection by:

1. Opening Cursor with the project
2. Checking the MCP server status in the bottom status bar
3. Testing AI commands that use MCP capabilities

## 🔧 Configuration Details

### **MCP Configuration File**
```json
{
  "mcpServers": {
    "github": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": {
        "GITHUB_TOKEN": "${env.PERSONAL_GITHUB_API_KEY}"
      }
    },
    "context7": {
      "url": "https://mcp.context7.com/mcp",
      "headers": {
        "CONTEXT7_API_KEY": "${env.PERSONAL_CONTEXT7_API_KEY}"
      }
    }
  }
}
```

### **Environment Variable Mapping**
- `PERSONAL_GITHUB_API_KEY` → `GITHUB_TOKEN` (for GitHub MCP server)
- `PERSONAL_CONTEXT7_API_KEY` → `CONTEXT7_API_KEY` (for Context7 MCP server)

## 🎯 Usage Examples

### **GitHub MCP Server Usage**

#### **Repository Operations**
```
"Show me the latest pull requests in this repository"
"Create a new issue for the bug I found"
"List all open issues with the 'bug' label"
"Show me the workflow runs for the last 7 days"
```

#### **Code Analysis**
```
"Analyze the code coverage in this repository"
"Show me all files that contain 'TODO' comments"
"Find all usages of the UserRepository class"
"Check for any security vulnerabilities in dependencies"
```

#### **Workflow Management**
```
"Show me the GitHub Actions workflow configuration"
"Trigger a workflow run for the main branch"
"View the logs for the last failed workflow"
"Update the workflow to use the latest Node.js version"
```

### **Context7 MCP Server Usage**

#### **Library Documentation**
```
"Show me how to use Ktor client for HTTP requests"
"Find examples of using Compose Multiplatform"
"Show me the best practices for Kotlin Multiplatform"
"Find documentation for Decompose navigation"
```

#### **Code Examples**
```
"Show me a complete example of a Compose screen"
"Find examples of using Koin for dependency injection"
"Show me how to implement error handling in KMP"
"Find patterns for state management in SwiftUI"
```

## 🛠️ Troubleshooting

### **Common Issues**

#### **MCP Server Not Connecting**
1. **Check Environment Variables**
   ```bash
   echo $PERSONAL_GITHUB_API_KEY
   echo $PERSONAL_CONTEXT7_API_KEY
   ```

2. **Verify API Keys**
   - GitHub: Test with `curl -H "Authorization: token YOUR_TOKEN" https://api.github.com/user`
   - Context7: Check your account dashboard

3. **Restart Cursor**
   - Close and reopen Cursor
   - Check the MCP server status in the bottom bar

#### **Permission Errors**
1. **GitHub Token Scopes**
   - Ensure your token has the required permissions
   - Check if the token has expired
   - Verify the token has access to the repository

2. **Context7 API Key**
   - Verify your account is active
   - Check if you've reached API limits
   - Ensure the key is correctly set

#### **Network Issues**
1. **Firewall/Proxy Settings**
   - Ensure Cursor can access external APIs
   - Check if corporate networks block the connections

2. **SSL/TLS Issues**
   - Update your system's CA certificates
   - Check if you're behind a corporate proxy

### **Debug Mode**

Enable debug logging for MCP servers:

1. Open Cursor settings
2. Search for "MCP" or "Model Context Protocol"
3. Enable debug logging
4. Check the output panel for detailed connection information

## 🔒 Security Considerations

### **API Key Security**
- **Never commit API keys** to version control
- **Use environment variables** or secure secret management
- **Rotate keys regularly** for security
- **Limit token scopes** to minimum required permissions

### **Access Control**
- **Review token permissions** regularly
- **Monitor API usage** for unusual activity
- **Use organization-level tokens** when possible
- **Implement IP restrictions** if supported

### **Data Privacy**
- **Be aware of what data** MCP servers can access
- **Review privacy policies** of external services
- **Limit sensitive data** in AI conversations
- **Use local MCP servers** for sensitive projects

## 📚 Advanced Configuration

### **Custom MCP Servers**

You can add additional MCP servers by extending the configuration:

```json
{
  "mcpServers": {
    "github": { /* existing config */ },
    "context7": { /* existing config */ },
    "custom-server": {
      "command": "path/to/your/server",
      "args": ["--config", "config.json"],
      "env": {
        "CUSTOM_API_KEY": "${env.CUSTOM_API_KEY}"
      }
    }
  }
}
```

### **Server-Specific Options**

#### **GitHub MCP Server Options**
```json
{
  "github": {
    "command": "npx",
    "args": ["-y", "@modelcontextprotocol/server-github"],
    "env": {
      "GITHUB_TOKEN": "${env.PERSONAL_GITHUB_API_KEY}",
      "GITHUB_API_URL": "https://api.github.com",
      "GITHUB_GRAPHQL_URL": "https://api.github.com/graphql"
    }
  }
}
```

#### **Context7 MCP Server Options**
```json
{
  "context7": {
    "url": "https://mcp.context7.com/mcp",
    "headers": {
      "CONTEXT7_API_KEY": "${env.PERSONAL_CONTEXT7_API_KEY}",
      "User-Agent": "YourApp/1.0"
    },
    "timeout": 30000
  }
}
```

## 🔗 Related Documentation

- [GitHub MCP Server](https://github.com/modelcontextprotocol/server-github) - Official GitHub MCP server
- [Context7 MCP Server](https://mcp.context7.com/) - Context7 MCP server documentation
- [Model Context Protocol](https://modelcontextprotocol.io/) - MCP specification and documentation
- [Cursor MCP Integration](https://cursor.sh/docs/mcp) - Cursor's MCP documentation

## 📝 Best Practices

### **1. Environment Management**
- Use `.env` files for local development
- Use secure secret management in production
- Never hardcode API keys in configuration files

### **2. Token Management**
- Use fine-grained tokens with minimal permissions
- Regularly rotate API keys
- Monitor token usage and access logs

### **3. Error Handling**
- Implement graceful fallbacks when MCP servers are unavailable
- Provide clear error messages for users
- Log connection issues for debugging

### **4. Performance Optimization**
- Cache frequently accessed data
- Implement request rate limiting
- Use connection pooling when possible

---

**Happy coding with enhanced AI assistance! 🚀**
