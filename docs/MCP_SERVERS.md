# MCP Servers Configuration

This project includes Model Context Protocol (MCP) server configurations for enhanced AI assistance in Cursor. MCP servers provide additional capabilities and context for AI-powered development.

## 📋 Table of Contents

- [🚀 What are MCP Servers?](#-what-are-mcp-servers)
- [📋 Available MCP Servers](#-available-mcp-servers)
- [⚙️ Installation & Setup](#-installation--setup)
- [🔧 Configuration Details](#-configuration-details)
- [🎯 Usage Examples](#-usage-examples)
- [🛠️ Troubleshooting](#-troubleshooting)
- [🔒 Security Considerations](#-security-considerations)
- [📚 Advanced Configuration](#-advanced-configuration)
- [🔗 Related Documentation](#-related-documentation)
- [📝 Best Practices](#-best-practices)

## 🚀 What are MCP Servers?

Model Context Protocol (MCP) servers are external services that provide additional context and capabilities to AI assistants. They enable AI to:
- Access external data sources
- Interact with APIs and services
- Provide real-time information
- Enhance development workflows

## 📋 Available MCP Servers

### 1. **Context7 MCP Server**
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
- Valid API keys for the services

### **Step 1: Install MCP Servers**

The MCP servers are configured in `.cursor/mcp.json` and will be automatically installed when you open the project in Cursor.

#### **Context7 MCP Server**
```bash
# The server is accessed via HTTPS
# No local installation required
```

### **Step 2: Configure Environment Variables**

Create a `.env` file in your project root or set these environment variables in your system:

```bash
# Context7 API Key
PERSONAL_CONTEXT7_API_KEY=your_context7_api_key_here
```

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
- `PERSONAL_CONTEXT7_API_KEY` → `CONTEXT7_API_KEY` (for Context7 MCP server)

## 🎯 Usage Examples

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
   echo $PERSONAL_CONTEXT7_API_KEY
   ```

2. **Verify API Keys**
   - Context7: Check your account dashboard

3. **Restart Cursor**
   - Close and reopen Cursor
   - Check the MCP server status in the bottom bar

#### **Permission Errors**
1. **Context7 API Key**
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
